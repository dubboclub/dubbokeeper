package com.dubboclub.dk.storage.model;

import java.io.Serializable;

/**
 * Created by qct on 2017/4/12.
 */
public class Service implements Serializable {

    private static final long serialVersionUID = 1704080694787990420L;

    private Integer id;
    private Integer appId;
    private Integer serviceId;
    private String serviceName;
    private Long timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
