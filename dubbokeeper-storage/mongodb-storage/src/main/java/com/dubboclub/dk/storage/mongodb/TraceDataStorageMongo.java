package com.dubboclub.dk.storage.mongodb;


import com.dubboclub.dk.storage.TraceDataHandler;
import com.dubboclub.dk.storage.TraceDataStorage;
import com.dubboclub.dk.tracing.api.Span;
import java.util.List;

/**
 * Created by Zetas on 2016/7/11.
 */
public class TraceDataStorageMongo implements TraceDataStorage {

    private List<TraceDataHandler> traceDataHandlerList;

    public void setTraceDataHandlerList(List<TraceDataHandler> traceDataHandlerList) {
        this.traceDataHandlerList = traceDataHandlerList;
    }

    public void addSpan(List<Span> spanList) {
        for (TraceDataHandler traceDataHandler : traceDataHandlerList) {
            traceDataHandler.handle(spanList);
        }
    }

}
