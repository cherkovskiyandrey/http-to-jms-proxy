package ru.sbrf.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ratpack.error.ServerErrorHandler;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.http.Status;
import ratpack.spring.config.EnableRatpack;
import ru.sbrf.ratpack.ErrorHandler;
import ru.sbrf.ratpack.FrontEndService;
import ru.sbrf.ratpack.Proxy;

import java.util.List;

@Configuration
@EnableRatpack
@EnableConfigurationProperties(RatpackExtraProperties.class)
public class RatpackConfig {
    private final Proxy proxy;
    private final FrontEndService frontEndService;
    private final List<ErrorHandler> errorHandlers;
    private final RatpackExtraProperties ratpackExtraProperties;

    @Autowired
    public RatpackConfig(Proxy proxy,
                         FrontEndService frontEndService,
                         List<ErrorHandler> errorHandlers,
                         RatpackExtraProperties ratpackExtraProperties) {
        this.proxy = proxy;
        this.frontEndService = frontEndService;
        this.errorHandlers = errorHandlers;
        this.ratpackExtraProperties = ratpackExtraProperties;
    }

    @Bean
    public Action<Chain> home() {
        return chain -> {
            registerDefaultErrorHandler(chain);
            registerProxyHandler(chain);
            registerAuditAjaxApi(chain);
        };
    }

    private void registerProxyHandler(Chain chain) {
        chain.post(ratpackExtraProperties.getProxyPath(), proxy);
    }

    private void registerDefaultErrorHandler(Chain chain) throws Exception {
        chain.register(registry ->
                registry.add(ServerErrorHandler.class, (context, throwable) -> {
                    if (errorHandlers.stream()
                            .noneMatch(h -> h.handle(context, throwable))) {
                        context.getResponse().status(Status.of(500));
                        context.getResponse().send(throwable.getMessage());
                    }
                })
        );
    }

    private void registerAuditAjaxApi(Chain chain) throws Exception {
        chain
                .files(c -> c.dir(ratpackExtraProperties.getPathToStaticContent()).indexFiles("index.html"))
                .get("stat", frontEndService::handleStatWSRequest)
                .prefix("history", chainHistory ->
                        chainHistory
                                .post(frontEndService::handleHistoryDataByFilter)
                                .get("body/:uuid", frontEndService::handleBodyByUUID)
                                .get("error/:uuid", frontEndService::handleErrorByUUID)
                );
    }
}
