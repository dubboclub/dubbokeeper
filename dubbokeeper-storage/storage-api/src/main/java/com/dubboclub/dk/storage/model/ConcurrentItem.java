package com.dubboclub.dk.storage.model;

/**
 * Created by bieber on 2015/11/4.
 */
public class ConcurrentItem extends  BaseItem{

    private long concurrent;

    public long getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(long concurrent) {
        this.concurrent = concurrent;
    }
}
