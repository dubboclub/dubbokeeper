package com.dubboclub.dk.storage.model;

import java.io.Serializable;

/**
 * Created by qct on 2017/4/12.
 */
public class Service implements Serializable {

    private static final long serialVersionUID = 1704080694787990420L;

    private Integer id;
    private String name;
    private Long timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Service{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", timestamp=" + timestamp +
            '}';
    }
}
