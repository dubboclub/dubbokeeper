package com.dubboclub.monitor.model;

/**
 * Created by bieber on 2015/11/4.
 */
public class OverviewItem {

    private long concurrent;

    private long elapsed;

    private long fault;

    private long success;

    public long getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(long concurrent) {
        this.concurrent = concurrent;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public long getFault() {
        return fault;
    }

    public void setFault(long fault) {
        this.fault = fault;
    }

    public long getSuccess() {
        return success;
    }

    public void setSuccess(long success) {
        this.success = success;
    }
}
