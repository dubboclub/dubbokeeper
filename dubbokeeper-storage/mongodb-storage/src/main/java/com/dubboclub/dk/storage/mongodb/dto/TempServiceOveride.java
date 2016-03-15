package com.dubboclub.dk.storage.mongodb.dto;

/**
 * Created by hideh on 2016/3/15.
 */
public class TempServiceOveride {

    private int remoteType;
    private String serviceInterface;

    public int getRemoteType() {
        return remoteType;
    }

    public void setRemoteType(int remoteType) {
        this.remoteType = remoteType;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }
}
