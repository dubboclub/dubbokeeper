package com.dubboclub.dk.sample1;

import com.dubboclub.dk.sample.api.ServiceA;
import com.dubboclub.dk.sample.api.ServiceB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>Created by Damon.Q on 2017/4/18.
 */
public class Consumer implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private ServiceB serviceB;

    public void setServiceB(ServiceB serviceB) {
        this.serviceB = serviceB;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (int i = 0; i < 1; i++) {
            logger.debug("calling: {}", serviceB.methodB("consumer"));
            Thread.sleep(2000l);
        }
    }
}
