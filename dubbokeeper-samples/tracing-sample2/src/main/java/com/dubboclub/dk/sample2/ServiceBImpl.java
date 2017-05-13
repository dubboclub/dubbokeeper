package com.dubboclub.dk.sample2;

import com.dubboclub.dk.sample.api.ServiceB;
import com.dubboclub.dk.sample.api.ServiceC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Created by Damon.Q on 2017/4/18.
 */
public class ServiceBImpl implements ServiceB {

    private static final Logger logger = LoggerFactory.getLogger(ServiceBImpl.class);

    private ServiceC serviceC;

    public void setServiceC(ServiceC serviceC) {
        this.serviceC = serviceC;
    }

    @Override
    public String methodB(String msg) {
        logger.debug("I'm methodB...");
        return msg + serviceC.methodC("--> methodB");
    }
}
