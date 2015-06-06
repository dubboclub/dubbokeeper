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
        Map<String,String> filter = new HashMap<String,String>();
        filter.put(Constants.APPLICATION_KEY,appName);
        Collection<Map.Entry<Long,URL>> urls = filterCategoryData(filter,Constants.CONSUMERS_CATEGORY);
        List<Consumer> consumers = new ArrayList<Consumer>();
        for(Map.Entry<Long,URL> url:urls){
            consumers.add(SyncUtils.url2Consumer(new Pair<Long, URL>(url)));
        }
        return consumers;
    }
}
