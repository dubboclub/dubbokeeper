package com.dubboclub.dk.sample3;

import com.dubboclub.dk.sample.api.ServiceC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Created by Damon.Q on 2017/4/18.
 */
public class ServiceCImpl implements ServiceC {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCImpl.class);

    @Override
    public String methodC(String msg) {
        logger.debug("I'm methodC...");
        return msg + "--> methodC";
    }
}
