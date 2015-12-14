package com.dubboclub.dk.admin.service;

import com.dubboclub.dk.admin.model.Consumer;

import java.util.List;

/**
 * Created by bieber on 2015/6/3.
 */
public interface ConsumerService {

    //获取某个应用的消费服务的列表
    public List<Consumer> listConsumerByApplication(String appName);

    //获取某个服务的所有消费者信息
    public List<Consumer> listConsumerByService(String service);

    //通过多条件来查询符合所有条件的消费者信息
    public List<Consumer> listConsumerByConditions(String... conditions);
}
