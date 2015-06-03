package com.dubboclub.controller;

import com.dubboclub.admin.model.Application;
import com.dubboclub.admin.model.Provider;
import com.dubboclub.admin.service.ApplicationService;
import com.dubboclub.admin.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping("/list.htm")
    public @ResponseBody List<Application> getApplications(){
        return applicationService.getApplications();
    }

    @RequestMapping("/{appName}/providers.htm")
    public @ResponseBody  List<Provider> getProviders(@PathVariable("appName")String appName){
        return providerService.listProviderByApplication(appName);
    }
}
