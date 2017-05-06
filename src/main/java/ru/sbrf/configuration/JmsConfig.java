package ru.sbrf.configuration;

//import com.ibm.mq.jms.MQQueueConnectionFactory;
//import com.ibm.msg.client.wmq.WMQConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@Configuration
@EnableConfigurationProperties(IBMMQProperties.class)
public class JmsConfig {

//    @Bean("singleConnectionFactory")
//    public ConnectionFactory connectionFactory(IBMMQProperties ibmmqConfig) throws JMSException {
//        final MQQueueConnectionFactory connectionFactory = new MQQueueConnectionFactory();
//
//        connectionFactory.setHostName(ibmmqConfig.getServerHost().getHostAddress());
//        connectionFactory.setPort(ibmmqConfig.getServerPort());
//        connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
//        connectionFactory.setQueueManager(ibmmqConfig.getQueueManager());
//        connectionFactory.setChannel(ibmmqConfig.getChannel());
//        connectionFactory.setClientReconnectTimeout(ibmmqConfig.getReconnectTimeout());
//
//        return connectionFactory;
//    }
//
//    @Bean("cachingConnectionFactory")
//    @Primary
//    public CachingConnectionFactory cachingConnectionFactory(@Qualifier("singleConnectionFactory") ConnectionFactory connectionFactory) {
//        final CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
//
//        cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
//        cachingConnectionFactory.setSessionCacheSize(100);
//        cachingConnectionFactory.setCacheConsumers(false);
//        cachingConnectionFactory.setCacheProducers(true);
//        cachingConnectionFactory.setReconnectOnException(true);
//
//        return cachingConnectionFactory;
//    }
//
//    @Bean
//    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, IBMMQProperties ibmmqConfig) {
//        final JmsTemplate jmsTemplate = new JmsTemplate();
//
//        jmsTemplate.setConnectionFactory(connectionFactory);
//        jmsTemplate.setSessionTransacted(false);
//        jmsTemplate.setDefaultDestinationName(ibmmqConfig.getQueueName());
//
//        return jmsTemplate;
//    }

}
