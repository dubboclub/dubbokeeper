package com.dubboclub.dk.web.model;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OverrideAbstractInfo that = (OverrideAbstractInfo) o;

        if (overrideCount != that.overrideCount) return false;
        if (applicationName != null ? !applicationName.equals(that.applicationName) : that.applicationName != null)
            return false;
        if (serviceKey != null ? !serviceKey.equals(that.serviceKey) : that.serviceKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = applicationName != null ? applicationName.hashCode() : 0;
        result = 31 * result + (serviceKey != null ? serviceKey.hashCode() : 0);
        result = 31 * result + overrideCount;
        return result;
    }
}
