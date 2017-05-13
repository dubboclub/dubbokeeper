package com.dubboclub.dk.web.controller;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.dubboclub.dk.admin.model.Application;
import com.dubboclub.dk.admin.model.Consumer;
import com.dubboclub.dk.admin.model.Node;
import com.dubboclub.dk.admin.model.Provider;
import com.dubboclub.dk.admin.service.ApplicationService;
import com.dubboclub.dk.admin.service.ConsumerService;
import com.dubboclub.dk.admin.service.ProviderService;
import com.dubboclub.dk.admin.sync.util.Tool;
import com.dubboclub.dk.web.model.AppConsumeInfo;
import com.dubboclub.dk.web.model.AppProvideInfo;
import com.dubboclub.dk.web.model.ConsumerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

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
     * 查询所有应用的服务列表数据
     * @return
     */
    @RequestMapping("/services.htm")
    public  @ResponseBody  List<AppProvideInfo> getServices() throws UnsupportedEncodingException {
        List<AppProvideInfo> provideInfoList = new ArrayList<AppProvideInfo>();
        Set<String> containMark = new HashSet<String>();
        StringBuilder protocolString = new StringBuilder();

        List<Provider> providers = providerService.listAllProvider();
        for(Provider provider : providers){
            if(containMark.contains(provider.getServiceKey())){
                continue;
            }
            containMark.add(provider.getServiceKey());
            AppProvideInfo provideInfo = new AppProvideInfo();
            provideInfo.setServiceKey(URLEncoder.encode(URLEncoder.encode(provider.getServiceKey(), "UTF-8"), "UTF-8"));
            provideInfo.setService(Tool.getInterface(provider.getServiceKey()));
            provideInfo.setVersion(provider.getVersion());
            provideInfo.setGroup(provider.getGroup());
            provideInfo.setId(provider.getId());
            List<Provider> providerList  = providerService.listProviderByConditions(
                    Constants.INTERFACE_KEY,
                    provideInfo.getService(),
                    Constants.GROUP_KEY,Tool.getGroup(provider.getServiceKey()),
                    Constants.VERSION_KEY,
                    Tool.getVersion(provider.getServiceKey()));
            for(Provider item:providerList){
                URL url = URL.valueOf(item.getUrl());
                protocolString
                        .append(url.getProtocol())
                        .append("://")
                        .append(url.getIp())
                        .append(":")
                        .append(url.getPort())
                        .append(",");
            }

            if(protocolString.length()>0){
                protocolString.setLength(protocolString.length()-1);
            }
            provideInfo.setProtocol(protocolString.toString());
            protocolString.setLength(0);
            provideInfoList.add(provideInfo);
        }
        return provideInfoList;
    }


    /**
     * 查询某个服务的消费者信息，从而可以确定该服务有多少消费者依赖它
     * @param id
     * @return
     */
    @RequestMapping("/{id}/consumer-apps.htm")
    public @ResponseBody List<Application> getConsumerAppByService(@PathVariable("id")long id ){
        Provider provider = providerService.getProviderById(id);
        List<Consumer> consumers = consumerService.listConsumerByConditions(Constants.INTERFACE_KEY,Tool.getInterface(provider.getServiceKey()),Constants.VERSION_KEY,Tool.getVersion(provider.getServiceKey()),Constants.GROUP_KEY,Tool.getGroup(provider.getServiceKey()));
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
            application.setType(Application.CONSUMER);
            List<Provider> providers = providerService.listProviderByApplication(consumer.getApplication());
            if(providers.size()>0){
                application.setType(Application.PROVIDER_AND_CONSUMER);
            }
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
    public @ResponseBody  List<AppProvideInfo> getProvides(@PathVariable("appName")String appName) throws UnsupportedEncodingException {
        List<Provider>  providers = providerService.listProviderByApplication(appName);
        List<AppProvideInfo> provideInfos = new ArrayList<AppProvideInfo>();
        Set<String> containMark = new HashSet<String>();
        StringBuilder protocolBuffer = new StringBuilder();
        for(Provider provider : providers){
            if(containMark.contains(provider.getServiceKey())){
                continue;
            }
            containMark.add(provider.getServiceKey());

            AppProvideInfo provideInfo = new AppProvideInfo();
            provideInfo.setServiceKey(URLEncoder.encode(URLEncoder.encode(provider.getServiceKey(), "UTF-8"), "UTF-8"));
            provideInfo.setService(Tool.getInterface(provider.getServiceKey()));
            provideInfo.setVersion(provider.getVersion());
            provideInfo.setGroup(provider.getGroup());
            provideInfo.setId(provider.getId());
            List<Provider> providerList  = providerService.listProviderByConditions(Constants.INTERFACE_KEY,provideInfo.getService(),Constants.GROUP_KEY,Tool.getGroup(provider.getServiceKey()),Constants.VERSION_KEY,Tool.getVersion(provider.getServiceKey()));
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
    public @ResponseBody  List<AppConsumeInfo> getConsumes(@PathVariable("appName")String appName) throws UnsupportedEncodingException {
        List<AppConsumeInfo> applicationConsumeInfos = new ArrayList<AppConsumeInfo>();
        List<Consumer> consumers =  consumerService.listConsumerByApplication(appName);
        for(Consumer consumer:consumers){
            AppConsumeInfo consumeInfo = new AppConsumeInfo();
            consumeInfo.setService(Tool.getInterface(consumer.getServiceKey()));
            consumeInfo.setServiceKey(URLEncoder.encode(URLEncoder.encode(consumer.getServiceKey(),"UTF-8"),"utf-8"));
            List<Provider> providers = providerService.listProviderByConditions(Constants.INTERFACE_KEY,Tool.getInterface(consumer.getServiceKey()),Constants.GROUP_KEY,Tool.getGroup(consumer.getServiceKey()),Constants.VERSION_KEY,Tool.getVersion(consumer.getServiceKey()));
            consumeInfo.setGroup(consumer.getGroup());
            consumeInfo.setVersion(consumer.getVersion());
            Map<String,String> params = Tool.convertParametersMap(consumer.getParameters());
            String accessProtocol = params.get(Constants.PROTOCOL_KEY);
            
            consumeInfo.setAccessProtocol(accessProtocol);
            if(providers.size()>0){
                consumeInfo.setProviderName(providers. get(0).getApplication());
                consumeInfo.setOwner(providers.get(0).getUsername());
                consumeInfo.setProviderCount(providers.size());
            }
            if(!applicationConsumeInfos.contains(consumeInfo)){
                applicationConsumeInfos.add(consumeInfo);
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
            if(containMark.contains(provider.getServiceKey())){
                continue;
            }
            containMark.add(provider.getServiceKey());
            ConsumerInfo consumerInfo = new ConsumerInfo();
            List<Consumer> consumers = consumerService.listConsumerByService(provider.getServiceKey());
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
    public @ResponseBody List<AppProvideInfo> geConsumeServiceInfoByConsumerAndProvider(@PathVariable("provider")String provider,@PathVariable("consumer")String consumer) throws UnsupportedEncodingException {
        List<AppProvideInfo> provideInfos = new ArrayList<AppProvideInfo>();
        List<Consumer> consumers= consumerService.listConsumerByApplication(consumer);
        List<String> containMark = new ArrayList<String>();
        StringBuffer protocolBuffer = new StringBuffer();
        for(Consumer consumerEntity:consumers){
            if(containMark.contains(consumerEntity.getServiceKey())){
                continue;
            }
            containMark.add(consumerEntity.getServiceKey());
            List<Provider> providers = providerService.listProviderByConditions(Constants.INTERFACE_KEY,Tool.getInterface(consumerEntity.getServiceKey()),Constants.VERSION_KEY,Tool.getVersion(consumerEntity.getServiceKey()),Constants.GROUP_KEY,Tool.getGroup(consumerEntity.getServiceKey()),Constants.APPLICATION_KEY,provider);
            if(providers.size()>0){
                AppProvideInfo provideInfo = new AppProvideInfo();
                Provider providerEntity = providers.get(0);
                provideInfo.setServiceKey(URLEncoder.encode(URLEncoder.encode(providerEntity.getServiceKey(), "UTF-8"), "UTF-8"));
                provideInfo.setService(Tool.getInterface(providerEntity.getServiceKey()));
                provideInfo.setVersion(providerEntity.getVersion());
                provideInfo.setGroup(providerEntity.getGroup());
                List<Provider> providerList = providerService.listProviderByServiceKey(providerEntity.getServiceKey());
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
