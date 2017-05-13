package com.dubboclub.dk.storage.model;

/**
 * Created by bieber on 2015/11/4.
 */
public class ConcurrentItem extends  BaseItem{

    private Long concurrent;

    public Long getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(Long concurrent) {
        this.concurrent = concurrent;
    }
}
