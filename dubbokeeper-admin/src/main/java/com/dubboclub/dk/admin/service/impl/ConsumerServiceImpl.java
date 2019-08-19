package com.dubboclub.dk.admin.service.impl;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.RegistryConstants;

import com.dubboclub.dk.admin.model.Consumer;
import com.dubboclub.dk.admin.service.AbstractService;
import com.dubboclub.dk.admin.service.ConsumerService;
import com.dubboclub.dk.admin.sync.util.Pair;
import com.dubboclub.dk.admin.sync.util.SyncUtils;

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
        }, RegistryConstants.CONSUMERS_CATEGORY,CommonConstants.APPLICATION_KEY,appName);
    }

    @Override
    public List<Consumer> listConsumerByService(String service) {
        return filterCategoryData(new ConvertURL2Entity<Consumer>() {
            @Override
            public Consumer convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Consumer(pair);
            }
        },RegistryConstants.CONSUMERS_CATEGORY,CommonConstants.INTERFACE_KEY,service);
    }

    @Override
    public List<Consumer> listConsumerByConditions(String... conditions) {
        return filterCategoryData(new ConvertURL2Entity<Consumer>() {
            @Override
            public Consumer convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Consumer(pair);
            }
        },RegistryConstants.CONSUMERS_CATEGORY,conditions);
    }
}
