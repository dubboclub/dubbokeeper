package com.dubboclub.model;

/**
 * Created by bieber on 2015/6/6.
 */
public class ApplicationProvideInfo {

    private String service;

    private String parameters;

    private int weight;

    private boolean dynamic;

    private boolean enabled;

    private String protocol;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationProvideInfo that = (ApplicationProvideInfo) o;

        if (dynamic != that.dynamic) return false;
        if (enabled != that.enabled) return false;
        if (weight != that.weight) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null) return false;
        if (service != null ? !service.equals(that.service) : that.service != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = service != null ? service.hashCode() : 0;
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + weight;
        result = 31 * result + (dynamic ? 1 : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
        return result;
    }
}
