package com.dubboclub.dk.sample1;

import com.dubboclub.dk.sample.api.ServiceA;
import com.dubboclub.dk.sample.api.ServiceB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Created by Damon.Q on 2017/4/18.
 */
public class ServiceAImpl implements ServiceA {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAImpl.class);

    private ServiceB serviceB;

    public void setServiceB(ServiceB serviceB) {
        this.serviceB = serviceB;
    }

    @Override
    public String methodA(String msg) {
        logger.debug("I'm methodA...");
        return msg + serviceB.methodB("--> methodA");
    }
}
