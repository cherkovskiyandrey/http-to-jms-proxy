package ru.cherkovskiy.jms;

import ru.cherkovskiy.domain.HttpRequest;

public interface JmsService {
    void syncSend(HttpRequest req);
}
