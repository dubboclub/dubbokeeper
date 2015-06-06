package com.dubboclub.controller;

import com.alibaba.dubbo.common.Constants;
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
import com.dubboclub.model.ConsumerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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


    /**
     * 查询所有应用列表信息
     * @return
     */
    @RequestMapping("/list.htm")
    public @ResponseBody List<Application> getApplications(){
        return applicationService.getApplications();
    }


    /**
     * 查询某个服务的消费者信息，从而可以确定该服务有多少消费者依赖它
     * @param service
     * @return
     */
    @RequestMapping("/{service}/consumer-apps.htm")
    public @ResponseBody List<Application> getConsumerAppByService(@PathVariable("service")String service ){
        List<Consumer> consumers = consumerService.listConsumerByService(service);
        List<Application> applicationList = new ArrayList<Application>();
        List<String> containMark = new ArrayList<String>();
        for(Consumer consumer:consumers){
            if(containMark.contains(consumer.getAddress())){
                continue;
            }
            containMark.add(consumer.getAddress());
            Application application = new Application();
            application.setUsername(consumer.getUsername());
            application.setApplication(consumer.getApplication());
            applicationList.add(application);
        }
        return applicationList;
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
            containMark.add(provider.getService());
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
     * 查找该应用需要消费哪些服务
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

    /**
     * 查找该应用有哪些消费者在使用它提供的服务
     * @param appName
     * @return
     */
    @RequestMapping("/{appName}/consumers.htm")
    public @ResponseBody List<ConsumerInfo> getConsumers(@PathVariable("appName")String appName){
        List<ConsumerInfo> consumerInfos = new ArrayList<ConsumerInfo>();
        List<Provider> providers = providerService.listProviderByApplication(appName);
        List<String> containMark = new ArrayList<String>();
        for(Provider provider:providers){
            if(containMark.contains(provider.getService())){
                continue;
            }
            containMark.add(provider.getService());
            ConsumerInfo consumerInfo = new ConsumerInfo();
            List<Consumer> consumers = consumerService.listConsumerByService(provider.getService());
            if(consumers.size()>0){
                consumerInfo.setParameters(consumers.get(0). getParameters());
                consumerInfo.setApplication(consumers.get(0).getApplication());
                consumerInfo.setUsername(consumers.get(0).getUsername());
                consumerInfos.add(consumerInfo);
            }
        }
        return consumerInfos;
    }

    /**
     * 查找某个消费者在制定指定提供者应用哪些服务
     * @param provider
     * @param consumer
     * @return
     */
    @RequestMapping("/{provider}/{consumer}/consumes.htm")
    public @ResponseBody List<ApplicationProvideInfo> geConsumeServiceInfoByConsumerAndProvider(@PathVariable("provider")String provider,@PathVariable("consumer")String consumer){
        List<ApplicationProvideInfo> provideInfos = new ArrayList<ApplicationProvideInfo>();
        List<Consumer> consumers= consumerService.listConsumerByApplication(consumer);
        List<String> containMark = new ArrayList<String>();
        StringBuffer protocolBuffer = new StringBuffer();
        for(Consumer consumerEntity:consumers){
            if(containMark.contains(consumerEntity.getService())){
                continue;
            }
            containMark.add(consumerEntity.getService());
            List<Provider> providers = providerService.listProviderByConditions(Constants.INTERFACE_KEY,consumerEntity.getService(),Constants.APPLICATION_KEY,provider);
            if(providers.size()>0){
                ApplicationProvideInfo provideInfo = new ApplicationProvideInfo();
                Provider providerEntity = providers.get(0);
                provideInfo.setService(providerEntity.getService());
                provideInfo.setDynamic(providerEntity.isDynamic());
                provideInfo.setEnabled(providerEntity.isEnabled());
                provideInfo.setParameters(consumerEntity.getParameters());
                provideInfo.setWeight(providerEntity.getWeight());
                List<Provider> providerList = providerService.listProviderByService(providerEntity.getService());
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
        }
        return provideInfos;
    }




}
