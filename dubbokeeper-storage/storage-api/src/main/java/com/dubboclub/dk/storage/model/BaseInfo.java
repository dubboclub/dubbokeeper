package com.dubboclub.dk.storage.model;

/**
 * Created by bieber on 2015/11/17.
 */
public abstract class BaseInfo {
    private long maxElapsed;


    private long maxConcurrent;

    private int maxFault;

    private int maxSuccess;

    public long getMaxElapsed() {
        return maxElapsed;
    }

    public void setMaxElapsed(long maxElapsed) {
        this.maxElapsed = maxElapsed;
    }

    public long getMaxConcurrent() {
        return maxConcurrent;
    }

    public void setMaxConcurrent(long maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    public int getMaxFault() {
        return maxFault;
    }

    public void setMaxFault(int maxFault) {
        this.maxFault = maxFault;
    }

    public int getMaxSuccess() {
        return maxSuccess;
    }

    public void setMaxSuccess(int maxSuccess) {
        this.maxSuccess = maxSuccess;
    }
}
