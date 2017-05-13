package com.dubboclub.dk.tracing.api;

import java.io.Serializable;

/**
 * Created by Zetas on 2016/7/7.
 */
public class BinaryAnnotation implements Serializable {

    private static final long serialVersionUID = 6197179344911907748L;

    private String key;
    private String value;
    private String type;
    private Integer duration;
    private Endpoint host;
    private Long timestamp;

    public Endpoint getHost() {
        return host;
    }

    public void setHost(Endpoint endpoint) {
        this.host = endpoint;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        return "BinaryAnnotation{" +
            "key='" + key + '\'' +
            ", value='" + value + '\'' +
            ", type='" + type + '\'' +
            ", duration=" + duration +
            ", host=" + host +
            ", timestamp=" + timestamp +
            '}';
    }
}
