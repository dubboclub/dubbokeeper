package com.dubboclub.dk.storage.model;

import java.io.Serializable;

/**
 * Created by Zetas on 2016/7/14.
 */
public class Application implements Serializable {

    private static final long serialVersionUID = -5878152761483422217L;

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
}
