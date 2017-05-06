package ru.sbrf.ratpack;

import org.springframework.stereotype.Component;
import ratpack.handling.Context;
import ratpack.http.Status;
import ru.sbrf.exceptions.HttpException;

@Component
public class CommonErrorHandler implements ErrorHandler {
    @Override
    public boolean handle(Context context, Throwable throwable) {
        if (!(throwable instanceof HttpException)) {
            return false;
        }
        final HttpException proxyException = (HttpException) throwable;
        context.getResponse().status(Status.of(proxyException.getCode()));
        context.getResponse().send(proxyException.getMessage());
        return true;
    }
}
