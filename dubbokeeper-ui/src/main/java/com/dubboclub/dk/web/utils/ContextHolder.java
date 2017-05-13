package com.dubboclub.dk.web.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by bieber on 2015/6/1.
 */
public class ContextHolder implements ApplicationContextAware{
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ContextUtils.setContext(applicationContext);
    }
}
