package com.dubboclub.admin.service;

import com.dubboclub.admin.model.Consumer;
import com.dubboclub.admin.model.Provider;

import java.util.List;

/**
 * Created by bieber on 2015/6/3.
 */
public interface ConsumerService {

    public List<Consumer> listConsumerByApplication(String appName);
}
