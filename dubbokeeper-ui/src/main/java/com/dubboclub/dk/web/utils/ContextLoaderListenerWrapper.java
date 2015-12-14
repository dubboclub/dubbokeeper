package com.dubboclub.dk.web.utils;

import org.springframework.web.context.ContextLoaderListener;

/**
 * Created by bieber on 2015/8/12.
 */
public class ContextLoaderListenerWrapper extends ContextLoaderListener {
    static {
        System.setProperty("dubbo.application.logger","slf4j");
        System.setProperty("io.netty.allocator.type","pooled");
    }
}
