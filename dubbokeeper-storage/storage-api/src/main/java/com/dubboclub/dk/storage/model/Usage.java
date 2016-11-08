package com.dubboclub.dk.storage.model;

import java.io.Serializable;

/**
 * Created by bieber on 2015/10/8.
 */
public class Usage implements Serializable {
    
    private String remoteAddress;
    
    private Long count;

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
