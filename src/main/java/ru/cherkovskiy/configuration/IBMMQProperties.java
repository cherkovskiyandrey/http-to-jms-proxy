package ru.cherkovskiy.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;

@ConfigurationProperties("ibm_mq")
public class IBMMQProperties {
    private InetAddress serverHost;
    private Integer serverPort;
    private String queueManager;
    private String channel;
    private Integer reconnectTimeout;
    private String queueName;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public InetAddress getServerHost() {
        return serverHost;
    }

    public void setServerHost(InetAddress serverHost) {
        this.serverHost = serverHost;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Integer getReconnectTimeout() {
        return reconnectTimeout;
    }

    public void setReconnectTimeout(Integer reconnectTimeout) {
        this.reconnectTimeout = reconnectTimeout;
    }

    @Override
    public String toString() {
        return "IBMMQProperties{" +
                "serverHost='" + serverHost + '\'' +
                ", serverPort=" + serverPort +
                ", queueManager='" + queueManager + '\'' +
                ", channel='" + channel + '\'' +
                ", reconnectTimeout=" + reconnectTimeout +
                '}';
    }
}
