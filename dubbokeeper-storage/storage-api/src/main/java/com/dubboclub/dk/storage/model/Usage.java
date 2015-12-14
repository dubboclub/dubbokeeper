package com.dubboclub.dk.storage.model;

/**
 * Created by bieber on 2015/10/8.
 */
public class Usage {
    
    private String remoteAddress;
    
    private long count;

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
