package com.dubboclub.dk.storage;

import com.dubboclub.dk.tracing.api.Span;
import java.util.List;

/**
 * <p>Created by Damon.Q on 2017/4/12.
 */
public abstract class AbstractTraceDataStorage implements TraceDataStorage {

    private List<TraceDataHandler> handlers;

    @Override
    public void storeTraceData(List<Span> spans) {
        for (Span span : spans) {
            for (TraceDataHandler handler : handlers) {
                handler.handle(span);
            }
        }
    }

    protected List<TraceDataHandler> getHandlers() {
        return handlers;
    }

    protected void setHandlers(List<TraceDataHandler> handlers) {
        this.handlers = handlers;
    }
}
