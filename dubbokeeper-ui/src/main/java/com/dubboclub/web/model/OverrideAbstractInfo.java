package com.dubboclub.web.model;

/**
 * Created by bieber on 2015/6/27.
 */
public class OverrideAbstractInfo {

    private String applicationName;

    private String serviceKey;

    private int overrideCount;

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

    public int getOverrideCount() {
        return overrideCount;
    }

    public void setOverrideCount(int overrideCount) {
        this.overrideCount = overrideCount;
    }
}
