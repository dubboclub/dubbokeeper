package com.dubboclub.controller;

import com.alibaba.dubbo.common.URL;
import com.dubboclub.admin.model.Application;
import com.dubboclub.admin.model.Consumer;
import com.dubboclub.admin.model.Node;
import com.dubboclub.admin.model.Provider;
import com.dubboclub.admin.service.ApplicationService;
import com.dubboclub.admin.service.ConsumerService;
import com.dubboclub.admin.service.ProviderService;
import com.dubboclub.model.ApplicationConsumeInfo;
import com.dubboclub.model.ApplicationProvideInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bieber on 2015/6/4.
 */
@Controller
@RequestMapping("/app")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConsumerService consumerService;


    @RequestMapping("/list.htm")
    public @ResponseBody List<Application> getApplications(){
        return applicationService.getApplications();
    }

    /**
     * 查找该应用部署在哪些服务器节点上
     * @param appName
     * @return
     */
    @RequestMapping("/{appName}/nodes.htm")
    public @ResponseBody  List<Node> getNodes(@PathVariable("appName")String appName){
        return applicationService.getNodesByApplicationName(appName);
    }

    /**
     * 查找该应用发布哪些服务
     * @param appName
     * @return
     */
    @RequestMapping("/{appName}/provides.htm")
    public @ResponseBody  List<ApplicationProvideInfo> getProvides(@PathVariable("appName")String appName){
        List<Provider>  providers = providerService.listProviderByApplication(appName);
        List<ApplicationProvideInfo> provideInfos = new ArrayList<ApplicationProvideInfo>();
        List<String> containMark = new ArrayList<String>();
        StringBuffer protocolBuffer = new StringBuffer();
        for(Provider provider : providers){
            if(containMark.contains(provider.getService())){
                continue;
            }
            ApplicationProvideInfo provideInfo = new ApplicationProvideInfo();
            provideInfo.setService(provider.getService());
            provideInfo.setDynamic(provider.isDynamic());
            provideInfo.setEnabled(provider.isEnabled());
            provideInfo.setParameters(provider.getParameters());
            provideInfo.setWeight(provider.getWeight());
            List<Provider> providerList = providerService.listProviderByService(provider.getService());
            for(Provider item:providerList){
                URL url = URL.valueOf(item.getUrl());
                protocolBuffer.append(url.getProtocol()).append(":").append(url.getPort()).append(",");
            }
            if(protocolBuffer.length()>0){
                protocolBuffer.setLength(protocolBuffer.length()-1);
            }
            provideInfo.setProtocol(protocolBuffer.toString());
            protocolBuffer.setLength(0);
            provideInfos.add(provideInfo);
        }
        return provideInfos;
    }

    /**
     * 查找该应用发布哪些服务
     * @param appName
     * @return
     */
    @RequestMapping("/{appName}/consumes.htm")
    public @ResponseBody  List<ApplicationConsumeInfo> getConsumes(@PathVariable("appName")String appName){
        List<ApplicationConsumeInfo> applicationConsumeInfos = new ArrayList<ApplicationConsumeInfo>();
        List<Consumer> consumers =  consumerService.listConsumerByApplication(appName);
        for(Consumer consumer:consumers){
            ApplicationConsumeInfo consumeInfo = new ApplicationConsumeInfo();
            consumeInfo.setService(consumer.getService());
            List<Provider> providers = providerService.listProviderByService(consumer.getService());
            if(providers.size()>0){
                consumeInfo.setProviderName(providers. get(0).getApplication());
                consumeInfo.setOwner(providers.get(0).getUsername());
                if(!applicationConsumeInfos.contains(consumeInfo)){
                    applicationConsumeInfos.add(consumeInfo);
                }
            }
        }
        return applicationConsumeInfos;
    }
}
