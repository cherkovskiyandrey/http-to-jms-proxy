package ru.sbrf.ratpack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Status;
import ratpack.http.TypedData;
import ratpack.rx.RxRatpack;
import ru.sbrf.audit.AuditService;
import ru.sbrf.domain.HttpRequest;
import ru.sbrf.exceptions.ProxyException;
import ru.sbrf.jms.JmsService;
import rx.schedulers.Schedulers;

@Component("Proxy")
public class Proxy implements Handler {

    private final AuditService auditService;
    private final JmsService jmsService;

    @Autowired
    public Proxy(AuditService auditService, JmsService jmsService) {
        this.auditService = auditService;
        this.jmsService = jmsService;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        final HttpRequest request = new HttpRequest(ctx.getRequest());
        auditService.saveOrUpdate(request);

        ctx.getRequest()
                .getBody(10*1024*1024,
                        () -> {throw ProxyException.of("Too large body. Max is 1 Mb.", 413, request);})
                .to(RxRatpack::observe)
                .map(TypedData::getBytes)
                .observeOn(Schedulers.io())
                .map(request::withBody)
                .doOnNext(auditService::saveOrUpdate)
                .doOnNext(jmsService::syncSend)
                .doOnError(this::handleError)
                .doOnNext(req -> auditService.saveOrUpdate(req.end()))
                .compose(RxRatpack::bindExec)
                .subscribe(s -> {
                    ctx.getResponse().status(Status.OK);
                    ctx.getResponse().send();
                });
    }

    private void handleError(Throwable throwable) {
        if(throwable instanceof ProxyException) {
            final ProxyException proxyException = (ProxyException)throwable;
            auditService.saveOrUpdate(proxyException.getHttpRequest().endWithError(throwable));
        }
    }
}
