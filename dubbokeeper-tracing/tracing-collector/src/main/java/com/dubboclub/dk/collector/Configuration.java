package com.dubboclub.dk.collector;

/**
 * Created by Zetas on 2016/7/11.
 */
public class Configuration {
    private Integer threadPoolSize;//收集器线程池大小

    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(Integer threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
}
