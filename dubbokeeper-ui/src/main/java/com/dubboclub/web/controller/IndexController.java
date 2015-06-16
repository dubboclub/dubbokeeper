package com.dubboclub.web.controller;

import com.dubboclub.admin.model.Application;
import com.dubboclub.admin.model.Provider;
import com.dubboclub.admin.service.ApplicationService;
import com.dubboclub.admin.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bieber on 2015/6/4.
 */
@Controller
public class IndexController {

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping("/index.htm")
    public String index(){
        return "index";
    }

    @RequestMapping("/loadAppsType.htm")
    public List<Integer> loadAppsTypeDoughnutChat(){
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
}
