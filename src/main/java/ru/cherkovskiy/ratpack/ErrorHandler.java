package ru.cherkovskiy.ratpack;

import ratpack.handling.Context;

public interface ErrorHandler {

    boolean handle(Context context, Throwable throwable);

}
