package com.dubboclub.admin.service.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.dubboclub.admin.model.Consumer;
import com.dubboclub.admin.service.AbstractService;
import com.dubboclub.admin.service.ConsumerService;
import com.dubboclub.admin.sync.util.Pair;
import com.dubboclub.admin.sync.util.SyncUtils;

import java.util.*;

/**
 * Created by bieber on 2015/6/6.
 */
public class ConsumerServiceImpl extends AbstractService implements ConsumerService {

    @Override
    public List<Consumer> listConsumerByApplication(String appName) {
        return filterCategoryData(new ConvertURL2Entity<Consumer>() {
            @Override
            public Consumer convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Consumer(pair);
            }
        },Constants.CONSUMERS_CATEGORY,Constants.APPLICATION_KEY,appName);
    }

    @Override
    public List<Consumer> listConsumerByService(String service) {
        return filterCategoryData(new ConvertURL2Entity<Consumer>() {
            @Override
            public Consumer convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Consumer(pair);
            }
        },Constants.CONSUMERS_CATEGORY,Constants.INTERFACE_KEY,service);
    }

    @Override
    public List<Consumer> listConsumerByConditions(String... conditions) {
        return filterCategoryData(new ConvertURL2Entity<Consumer>() {
            @Override
            public Consumer convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Consumer(pair);
            }
        },Constants.CONSUMERS_CATEGORY,conditions);
    }
}
