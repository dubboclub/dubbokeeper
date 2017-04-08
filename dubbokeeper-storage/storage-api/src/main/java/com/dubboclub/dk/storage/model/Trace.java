package com.dubboclub.dk.storage.model;

import java.io.Serializable;

/**
 * Created by Zetas on 2016/7/14.
 */
public class Trace implements Serializable {
    private String traceId;
    private String serviceName;
    private Integer duration;
    private Long time;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}
