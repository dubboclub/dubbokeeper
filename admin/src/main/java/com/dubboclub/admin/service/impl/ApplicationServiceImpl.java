package com.dubboclub.admin.service.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.dubboclub.admin.model.Application;
import com.dubboclub.admin.service.AbstractService;
import com.dubboclub.admin.service.ApplicationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by bieber on 2015/6/4.
 */
public class ApplicationServiceImpl extends AbstractService implements ApplicationService {
    @Override
    public List<Application> getApplications() {
        ConcurrentMap<String, Map<Long, URL>> providers = getServiceByCategory(Constants.PROVIDERS_CATEGORY);
        ConcurrentMap<String, Map<Long, URL>> consumers = getServiceByCategory(Constants.CONSUMERS_CATEGORY);
        List<Application> applications = new ArrayList<Application>();
        if(providers!=null){
            for(Map.Entry<String, Map<Long, URL>> oneService:providers.entrySet()){
                Map<Long, URL> urls = oneService.getValue();
                for(Map.Entry<Long,URL> url:urls.entrySet()){
                    Application application = new Application();
                    application.setApplication(url.getValue().getParameter(Constants.APPLICATION_KEY));
                    application.setUsername(url.getValue().getParameter("owner"));
                    if(!applications.contains(application)){
                        applications.add(application);
                    }
                    break;
                }
            }
        }
        if(consumers!=null){
            for(Map.Entry<String, Map<Long, URL>> oneService:consumers.entrySet()){
                Map<Long, URL> urls = oneService.getValue();
                for(Map.Entry<Long,URL> url:urls.entrySet()){
                    Application application = new Application();
                    application.setApplication(url.getValue().getParameter(Constants.APPLICATION_KEY));
                    application.setUsername(url.getValue().getParameter("owner"));
                    if(!applications.contains(application)){
                        applications.add(application);
                    }
                    break;
                }
            }
        }
        return applications;
    }
}
