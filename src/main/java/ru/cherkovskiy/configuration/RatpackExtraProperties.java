package ru.cherkovskiy.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ratpack-extra")
public class RatpackExtraProperties {
    private String pathToStaticContent = "static";
    private String proxyPath = "proxy";

    public String getPathToStaticContent() {
        return pathToStaticContent;
    }

    public void setPathToStaticContent(String pathToStaticContent) {
        this.pathToStaticContent = pathToStaticContent;
    }

    public String getProxyPath() {
        return proxyPath;
    }

    public void setProxyPath(String proxyPath) {
        this.proxyPath = proxyPath;
    }

    @Override
    public String toString() {
        return "RatpackExtraProperties{" +
                "pathToStaticContent='" + pathToStaticContent + '\'' +
                ", proxyPath='" + proxyPath + '\'' +
                '}';
    }
}
