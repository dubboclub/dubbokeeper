package com.dubboclub.dk.storage.mongodb;


import com.dubboclub.dk.storage.model.Application;
import com.dubboclub.dk.storage.model.Service;
import com.dubboclub.dk.storage.mongodb.dao.TracingApplicationDao;
import com.dubboclub.dk.storage.mongodb.dao.TracingServiceDao;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by Zetas on 2016/7/14.
 */
public class ContextHolder implements InitializingBean {

    private final ConcurrentMap<Integer, Application> applicationMap;
    private final ConcurrentMap<Integer, Service> serviceMap;
    private TracingApplicationDao applicationDao;
    private TracingServiceDao serviceDao;

    public ContextHolder() {
        applicationMap = new ConcurrentHashMap<Integer, Application>();
        serviceMap = new ConcurrentHashMap<Integer, Service>();
    }

    public void setTracingApplicationDao(TracingApplicationDao applicationDao) {
        this.applicationDao = applicationDao;
    }

    public void setTracingServiceDao(TracingServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }

    public Application findTracingApplicatonById(Integer applicationId) {
        Application application = applicationMap.get(applicationId);
        if (application == null) {
            application = loadTracingApplication(applicationId);
        }
        return application;
    }

    public Service findTracingServiceById(Integer serviceId) {
        Service service = serviceMap.get(serviceId);
        if (service == null) {
            service = loadTracingService(serviceId);
        }
        return service;
    }

    private synchronized Application loadTracingApplication(Integer applicationId) {
        Application application = applicationDao.findById(applicationId);
        if (application != null) {
            applicationMap.put(application.getId(), application);
        }
        return application;
    }

    private synchronized Service loadTracingService(Integer serviceId) {
        Service service = serviceDao.findId(serviceId);
        if (service != null) {
            serviceMap.put(service.getId(), service);
        }
        return service;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
