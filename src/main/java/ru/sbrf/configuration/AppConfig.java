package ru.sbrf.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.sbrf.HttpToJmsProxyServer;

@Configuration
@ComponentScan(basePackageClasses = HttpToJmsProxyServer.class)
public class AppConfig {
}
