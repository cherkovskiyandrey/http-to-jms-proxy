package ru.cherkovskiy.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.cherkovskiy.HttpToJmsProxyServer;

@Configuration
@ComponentScan(basePackageClasses = HttpToJmsProxyServer.class)
public class AppConfig {
}
