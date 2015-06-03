package com.dubboclub.admin.service;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.dubboclub.admin.sync.RegistryServerSync;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by bieber on 2015/6/3.
 */
public abstract class AbstractService {

    private RegistryServerSync registryServerSync;


    public void setRegistryServerSync(RegistryServerSync registryServerSync) {
        this.registryServerSync = registryServerSync;
    }

    protected ConcurrentMap<String, Map<Long, URL>> getServiceByCategory(String category){
        return registryServerSync.getRegistryCache().get(category);
    }

    protected Collection<Map.Entry<Long,URL>> filterCategoryData(Map<String,String> filter,String category){
        ConcurrentMap<String, Map<Long, URL>> services = getServiceByCategory(category);
        List<Map.Entry<Long,URL>> matchedData = new ArrayList<Map.Entry<Long,URL>>();
        //该目录下面所有的服务信息
        if(services!=null){
            //某个服务下面所有的配置信息
            //默认情况一个服务只属于一个提供者应用
            Collection<Map<Long, URL>> servicesUrls = services.values();
            for(Map<Long, URL> urls:servicesUrls){
                for(Map.Entry<Long,URL> entry:urls.entrySet()){
                    Map<String,String> parameters = entry.getValue().getParameters();
                    if(parameters!=null){
                        boolean matched=true;
                        for(Map.Entry<String,String> filterEntry:filter.entrySet()){
                           if(!parameters.containsKey(filterEntry.getKey())||!filterEntry.getValue().equals(parameters.get(filterEntry.getKey()))){
                               matched=false;
                               break;
                           }
                        }
                        if(matched){
                            matchedData.add(entry);
                        }
                    }
                }
            }
        }
        return matchedData;
    }



}
