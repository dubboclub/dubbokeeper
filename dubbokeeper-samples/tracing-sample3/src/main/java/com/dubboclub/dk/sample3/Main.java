package com.dubboclub.dk.sample3;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <p>Created by qct on 2017/4/18.
 */
public class Main {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext( "classpath*:META-INF/spring/*.xml");
        context.start();
    }
}
