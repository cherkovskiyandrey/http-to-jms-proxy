package ru.sbrf.jms;

import ru.sbrf.domain.HttpRequest;

public interface JmsService {
    void syncSend(HttpRequest req);
}
