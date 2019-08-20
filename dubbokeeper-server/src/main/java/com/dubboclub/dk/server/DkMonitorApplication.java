package com.dubboclub.dk.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(exclude = MongoRepositoriesAutoConfiguration.class)
@ImportResource("classpath*:META-INF/spring/*.xml")
public class DkMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(DkMonitorApplication.class);
    }
}
