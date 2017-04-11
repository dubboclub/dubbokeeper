package com.dubboclub.dk.storage.mongodb;


import com.dubboclub.dk.storage.mongodb.dao.TracingApplicationDao;
import com.dubboclub.dk.storage.mongodb.dao.TracingServiceDao;
import com.dubboclub.dk.storage.mongodb.dto.TracingApplicationDto;
import com.dubboclub.dk.storage.mongodb.dto.TracingServiceDto;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by Zetas on 2016/7/14.
 */
public class ContextHolder implements InitializingBean {

    private final ConcurrentMap<Integer, TracingApplicationDto> applicationMap;
    private final ConcurrentMap<Integer, TracingServiceDto> serviceMap;
    private TracingApplicationDao applicationDao;
    private TracingServiceDao serviceDao;

    public ContextHolder() {
        applicationMap = new ConcurrentHashMap<Integer, TracingApplicationDto>();
        serviceMap = new ConcurrentHashMap<Integer, TracingServiceDto>();
    }

    public void setTracingApplicationDao(TracingApplicationDao applicationDao) {
        this.applicationDao = applicationDao;
    }

    public void setTracingServiceDao(TracingServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }

    public TracingApplicationDto findTracingApplicatonDtoById(Integer applicationId) {
        TracingApplicationDto dto = applicationMap.get(applicationId);
        if (dto == null) {
            dto = loadTracingApplicationDto(applicationId);
        }
        return dto;
    }

    public TracingServiceDto findTracingServiceDtoById(Integer serviceId) {
        TracingServiceDto dto = serviceMap.get(serviceId);
        if (dto == null) {
            dto = loadTracingServiceDto(serviceId);
        }
        return dto;
    }

    private synchronized TracingApplicationDto loadTracingApplicationDto(Integer applicationId) {
        TracingApplicationDto dto = applicationDao.findOneByApplicationId(applicationId);
        if (dto != null) {
            applicationMap.put(dto.getApplicationId(), dto);
        }
        return dto;
    }

    private synchronized TracingServiceDto loadTracingServiceDto(Integer serviceId) {
        TracingServiceDto dto = serviceDao.findOneByServiceId(serviceId);
        if (dto != null) {
            serviceMap.put(dto.getServiceId(), dto);
        }
        return dto;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
