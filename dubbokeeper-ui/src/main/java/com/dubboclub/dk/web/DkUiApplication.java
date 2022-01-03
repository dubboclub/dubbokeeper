package com.dubboclub.dk.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath*:META-INF/spring/*.xml")
public class DkUiApplication {
    public static void main(String[] args) {
//        System.setProperty("dubbo.application.logger", "slf4j");
//        System.setProperty("io.netty.allocator.type", "pooled");
        SpringApplication.run(DkUiApplication.class);
    }
}
