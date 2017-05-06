package ru.cherkovskiy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ratpack.rx.RxRatpack;
import ru.cherkovskiy.configuration.AppConfig;

@SpringBootApplication
public class HttpToJmsProxyServer {

    public static void main(String[] args) {
        RxRatpack.initialize() ;
        SpringApplication.run(AppConfig.class);
    }
}

