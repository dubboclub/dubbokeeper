package com.dubboclub.dk.storage.model;

import java.io.Serializable;

/**
 * Created by bieber on 2015/11/17.
 */
public abstract class BaseInfo implements Serializable {
    private Long maxElapsed;


    private Long maxConcurrent;

    private Integer maxFault;

    private Integer maxSuccess;

    public Long getMaxElapsed() {
        return maxElapsed;
    }

    public void setMaxElapsed(Long maxElapsed) {
        this.maxElapsed = maxElapsed;
    }

    public Long getMaxConcurrent() {
        return maxConcurrent;
    }

    public void setMaxConcurrent(Long maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    public Integer getMaxFault() {
        return maxFault;
    }

    public void setMaxFault(Integer maxFault) {
        this.maxFault = maxFault;
    }

    public Integer getMaxSuccess() {
        return maxSuccess;
    }

    public void setMaxSuccess(Integer maxSuccess) {
        this.maxSuccess = maxSuccess;
    }
}
