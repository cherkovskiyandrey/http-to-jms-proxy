package ru.sbrf.ratpack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ratpack.handling.Context;
import ratpack.http.Status;
import ratpack.http.TypedData;
import ratpack.rx.RxRatpack;
import ratpack.websocket.WebSockets;
import ru.sbrf.audit.AuditService;
import ru.sbrf.domain.history.HistoryFilter;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;

@Component
public class FrontEndService {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final AuditService auditService;

    static {
        objectMapper.findAndRegisterModules();
    }

    @Autowired
    public FrontEndService(AuditService auditService) {
        this.auditService = auditService;
    }

    public static <T> String toJson(T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String obj, Class<T> cls) {
        try {
            return objectMapper.readValue(obj, cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleStatWSRequest(Context ctx) {

        final Observable<String> status = auditService.getCurrentStatus()
                .map(FrontEndService::toJson);

        WebSockets.websocketBroadcast(ctx, RxRatpack.publisher(status));
    }

    public void handleHistoryDataByFilter(Context ctx) throws Exception {
        ctx.getRequest()
                .getBody()
                .to(RxRatpack::observe)
                .map(TypedData::getText)
                .observeOn(Schedulers.computation())
                .map(s -> fromJson(s, HistoryFilter.class))
                .flatMap(auditService::getHistoryBy)
                .map(FrontEndService::toJson)
                .compose(RxRatpack::bindExec)
                .subscribe(s -> {
                    ctx.getResponse().status(Status.OK);
                    ctx.getResponse().send(s);
                });
    }

    public void handleBodyByUUID(Context ctx) {
        final String uuid = ctx.getPathTokens().get("uuid");
        auditService.getBodyByUUID(uuid)
                .compose(RxRatpack::bindExec)
                .subscribe(s -> {
                    ctx.getResponse().status(Status.OK);
                    ctx.getResponse().send(s);
                });
    }

    public void handleErrorByUUID(Context ctx) {
        final String uuid = ctx.getPathTokens().get("uuid");
        auditService.getErrorByUUID(uuid)
                .compose(RxRatpack::bindExec)
                .subscribe(s -> {
                    ctx.getResponse().status(Status.OK);
                    ctx.getResponse().send(s);
                });
    }
}
