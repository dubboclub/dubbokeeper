package com.dubboclub.dk.web.model;

/**
 * Created by bieber on 2015/8/1.
 */
public class RouteAbstractInfo {

    private String applicationName;

    private String serviceKey;

    private int routeCount;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public int getRouteCount() {
        return routeCount;
    }

    public void setRouteCount(int routeCount) {
        this.routeCount = routeCount;
    }
}
