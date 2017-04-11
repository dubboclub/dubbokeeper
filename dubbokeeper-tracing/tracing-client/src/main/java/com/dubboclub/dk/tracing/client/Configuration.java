package com.dubboclub.dk.tracing.client;

/**
 * Created by Zetas on 2016/7/8.
 */
public class Configuration {

    private Integer flushSize;//一批发送多少条消息到收集端
    private Integer queueSize;//缓冲队列大小

    public Integer getFlushSize() {
        return flushSize;
    }

    public void setFlushSize(Integer flushSize) {
        this.flushSize = flushSize;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }
}
