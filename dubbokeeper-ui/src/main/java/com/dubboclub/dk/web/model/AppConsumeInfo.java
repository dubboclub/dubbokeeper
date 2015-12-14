package com.dubboclub.dk.web.model;

/**
 * Created by bieber on 2015/6/6.
 * 应用消费服务信息
 */
public class AppConsumeInfo extends BasicResponse{

    private String service;
    
    private String serviceKey;

    private String providerName;

    private String owner;

    private String group;

    private String version;
    private String accessProtocol;
    private int providerCount;

    public String getAccessProtocol() {
        return accessProtocol;
    }
    
    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public void setAccessProtocol(String accessProtocol) {
        this.accessProtocol = accessProtocol;
    }

    public int getProviderCount() {
        return providerCount;
    }

    public void setProviderCount(int providerCount) {
        this.providerCount = providerCount;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppConsumeInfo that = (AppConsumeInfo) o;

        if (group != null ? !group.equals(that.group) : that.group != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (providerName != null ? !providerName.equals(that.providerName) : that.providerName != null) return false;
        if (service != null ? !service.equals(that.service) : that.service != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = service != null ? service.hashCode() : 0;
        result = 31 * result + (providerName != null ? providerName.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
