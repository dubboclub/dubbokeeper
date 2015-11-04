package com.dubboclub.monitor.model;

/**
 * Created by bieber on 2015/11/4.
 */
public class BaseItem {

    private String method;

    private String service;

    private long timestamp;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
