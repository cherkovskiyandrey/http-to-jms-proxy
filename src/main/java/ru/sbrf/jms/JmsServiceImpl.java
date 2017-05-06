package ru.sbrf.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.sbrf.domain.HttpRequest;
import ru.sbrf.exceptions.ProxyException;

import javax.jms.*;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class JmsServiceImpl implements JmsService {
//    private final JmsTemplate jmsTemplate;
//
//    @Autowired
//    public JmsServiceImpl(JmsTemplate jmsTemplate) {
//        this.jmsTemplate = jmsTemplate;
//    }

    @Override
    public void syncSend(final HttpRequest request) {
        try {
            if(ThreadLocalRandom.current().nextBoolean()) {
                throw new RuntimeException("Just test exception.");
            }
          //  jmsTemplate.send(session -> MessageBuilder.valueOf(request).createMessage(session));
        } catch (Exception ex) {
            throw new ProxyException(ex.getMessage(), ex, 503, request);
        }
    }

    @FunctionalInterface
    private interface MessageBuilder {

        Message createMessage(Session session) throws JMSException;

        static MessageBuilder valueOf(HttpRequest request) {
            if(request.getContentType().isText() || request.getContentType().isHtml() || request.getContentType().isJson()) {

                return session -> {
                    final String charsetIn = request.getContentType().getCharset("UTF-8");
                    final TextMessage textMessage = session.createTextMessage();
                    textMessage.setText(new String(request.getBody(), Charset.forName(charsetIn)));
                    textMessage.setJMSCorrelationID(request.getUUID());
                    textMessage.setStringProperty("JmsHttpReceivedTimestamp", request.getReceivedTimestamp().toString());
                    textMessage.setStringProperty("JmsHttpSourceAddress", request.getRemoteAddress().getHost());
                    return textMessage;
                };
            }

            return session -> {
                final BytesMessage bytesMessage = session.createBytesMessage();
                bytesMessage.writeBytes(request.getBody());
                bytesMessage.setJMSCorrelationID(request.getUUID());
                bytesMessage.setStringProperty("JmsHttpReceivedTimestamp", request.getReceivedTimestamp().toString());
                bytesMessage.setStringProperty("JmsHttpSourceAddress", request.getRemoteAddress().getHost());
                return bytesMessage;
            };
        }
    }
}
