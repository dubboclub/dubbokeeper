package com.dubboclub.web.controller;

import com.alibaba.dubbo.common.URL;
import com.dubboclub.admin.model.Application;
import com.dubboclub.admin.model.Consumer;
import com.dubboclub.admin.model.Provider;
import com.dubboclub.admin.service.ApplicationService;
import com.dubboclub.admin.service.ConsumerService;
import com.dubboclub.admin.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bieber on 2015/6/4.
 */
@Controller
public class IndexController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ConsumerService consumerService;

    @RequestMapping("/index.htm")
    public String index(){
        return "index";
    }

    @RequestMapping("/loadAppsType.htm")
    public @ResponseBody List<Integer> loadAppsTypeDoughnutChat(){
        List<Application> applications = applicationService.getApplications();
        int providerCount=0;
        int consumerCount=0;
        int providerAndConsumerCount=-0;
        for(Application application:applications){
            if(application.getType()==Application.PROVIDER){
                providerCount++;
            }else if(application.getType()==Application.CONSUMER){
                consumerCount++;
            }else{
                providerAndConsumerCount++;
            }
        }
        List<Integer> statistics = new ArrayList<Integer>();
        statistics.add(providerCount);
        statistics.add(consumerCount);
        statistics.add(providerAndConsumerCount);
        return statistics;
    }


    @RequestMapping("/loadServiceProtocols.htm")
    public  @ResponseBody Map<String,Integer> loadServiceProtocolDoughnutChat(){
        List<Provider> providers = providerService.listAllProvider();
         Map<String,Integer> statistics = new HashMap<String, Integer>();
        for(Provider provider:providers){
            URL providerUrl = URL.valueOf(provider.getUrl());
            if(statistics.containsKey(providerUrl.getProtocol())){
                statistics.put(providerUrl.getProtocol(),statistics.get(providerUrl.getProtocol())+1);
            }else{
                statistics.put(providerUrl.getProtocol(),1);
            }
        }
        return statistics;
    }

    @RequestMapping("/loadAppServices.htm")
    public  @ResponseBody Map<String,List<Integer>> loadApplicationServiceStatus(){
        List<Application> applications =  applicationService.getApplications();
        Map< String,List<Integer>> statistics =  new HashMap<String, List<Integer>>();
        for(Application application:applications){
            List<Integer> data = new ArrayList<Integer>();
            data.add(providerService.listProviderByApplication(application.getApplication()).size());
            data.add(consumerService.listConsumerByApplication(application.getApplication()).size());
            statistics.put(application.getApplication(),data);
        }
        return statistics;
    }


    @RequestMapping("/loadAppsDependencies.htm")
    public Map<String,List<Map<String,Object>>> loadAppsDependencies(){
        Map<String,List<Map<String,Object>>> statistics=new HashMap<String, List<Map<String, Object>>>();
        List<Application> applications =  applicationService.getApplications();
        List<Map<String,Object>> nodes = new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> links = new ArrayList<Map<String, Object>>();
        for(Application application:applications){
            Map<String,Object> node = new HashMap<String, Object>();
            node.put("category",application.getType()-1);
            node.put("name",application.getApplication());
            node.put("value",application.getApplication());
            node.put("symbolSize",20);
            node.put("draggable",true);
            nodes.add(node);
            List<Consumer> consumers = consumerService.listConsumerByApplication(application.getApplication());
            for(Consumer consumer:consumers){
                Map<String,Object> link = new HashMap<String, Object>();
                link.put("source",application.getApplication());
                List<Provider> providers = providerService.listProviderByServiceKey(consumer.getService());
                if(providers.size()>0){
                    link.put("target",providers.get(0).getApplication());
                    link.put("weight",1);
                    link.put("name",application.getApplication()+"依赖"+providers.get(0).getApplication());
                    links.add(link);
                }
            }
        }
        statistics.put("nodes",nodes);
        statistics.put("links",links);
        return statistics;
    }
}
