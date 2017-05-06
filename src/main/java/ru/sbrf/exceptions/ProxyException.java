package ru.sbrf.exceptions;

import ru.sbrf.domain.HttpRequest;

public class ProxyException extends HttpException {
    private final HttpRequest httpRequest;

    public ProxyException(String message, int code, HttpRequest httpRequest) {
        super(message, code);
        this.httpRequest = httpRequest;
    }

    public ProxyException(String message, Throwable cause, int code, HttpRequest httpRequest) {
        super(message, cause, code);
        this.httpRequest = httpRequest;
    }

    public static ProxyException of(String message, int code, HttpRequest httpRequest) {
        return new ProxyException(message, code, httpRequest);
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }
}
