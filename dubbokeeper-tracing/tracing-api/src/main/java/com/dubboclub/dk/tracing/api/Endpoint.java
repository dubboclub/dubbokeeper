package com.dubboclub.dk.tracing.api;

import java.io.Serializable;

/**
 * Created by Zetas on 2016/7/7.
 */
public class Endpoint implements Serializable {
    private String ip;
    private Integer port;
    private String applicationName;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", applicationName='" + applicationName + '\'' +
                '}';
    }
}
