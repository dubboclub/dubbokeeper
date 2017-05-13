package com.dubboclub.dk.storage.model;

import java.io.Serializable;

/**
 * <p>Created by qct on 2017/4/12.
 */
public class Trace implements Serializable {

    private static final long serialVersionUID = 3714361831053044723L;

    private Long id;
    private Long traceId;
    private Integer serviceId;
    private Integer duration;
    private Long timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTraceId() {
        return traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Trace{" +
            "id=" + id +
            ", serviceId=" + serviceId +
            ", duration=" + duration +
            ", timestamp=" + timestamp +
            '}';
    }
}
