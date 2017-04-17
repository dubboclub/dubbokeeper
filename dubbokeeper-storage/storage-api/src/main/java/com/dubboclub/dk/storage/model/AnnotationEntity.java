package com.dubboclub.dk.storage.model;

import java.io.Serializable;

/**
 * Created by qct on 2017/4/12.
 */
public class AnnotationEntity implements Serializable {

    private static final long serialVersionUID = 7914586656880862607L;

    private Long id;
    private String key;
    private String value;
    private String ip;
    private Integer port;
    private Long timestamp;
    private Integer duration;

    private Long spanId;
    private Long traceId;
    private Integer serviceId;
    private String serviceName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getSpanId() {
        return spanId;
    }

    public void setSpanId(Long spanId) {
        this.spanId = spanId;
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "Annotation{" +
            "id=" + id +
            ", key='" + key + '\'' +
            ", value='" + value + '\'' +
            ", ip='" + ip + '\'' +
            ", port=" + port +
            ", timestamp=" + timestamp +
            ", duration=" + duration +
            ", spanId=" + spanId +
            ", traceId=" + traceId +
            ", serviceId=" + serviceId +
            ", serviceName='" + serviceName + '\'' +
            '}';
    }
}