package ru.cherkovskiy.exceptions;


public class HttpException extends RuntimeException {
    private final int code;

    public HttpException(String message, int code) {
        super(message);
        this.code = code;
    }

    public HttpException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static HttpException of(String message, int code) {
        return new HttpException(message, code);
    }
}
