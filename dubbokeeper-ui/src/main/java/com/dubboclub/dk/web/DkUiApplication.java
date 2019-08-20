package com.dubboclub.dk.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.util.StringUtils;

import static org.apache.dubbo.common.constants.CommonConstants.DUBBO_PROPERTIES_KEY;

@SpringBootApplication
@ImportResource("classpath*:META-INF/spring/*.xml")
public class DkUiApplication {
    public static void main(String[] args) {
        System.setProperty("dubbo.application.logger", "slf4j");
        System.setProperty("io.netty.allocator.type", "pooled");
        if (!StringUtils.hasText(System.getProperty(DUBBO_PROPERTIES_KEY))) {
            System.setProperty(DUBBO_PROPERTIES_KEY, "application-ui.properties");
        }

        SpringApplication.run(DkUiApplication.class);
    }
}
