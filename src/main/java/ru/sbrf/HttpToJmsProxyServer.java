package ru.sbrf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ratpack.rx.RxRatpack;
import ru.sbrf.configuration.AppConfig;

@SpringBootApplication
public class HttpToJmsProxyServer {

    public static void main(String[] args) {
        RxRatpack.initialize() ;
        SpringApplication.run(AppConfig.class);
    }
}

