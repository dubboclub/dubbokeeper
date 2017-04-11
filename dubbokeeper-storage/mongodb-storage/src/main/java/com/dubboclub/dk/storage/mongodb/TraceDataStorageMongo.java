package com.dubboclub.dk.storage.mongodb;


import com.dubboclub.dk.storage.TraceDataStorage;
import com.dubboclub.dk.tracing.api.Span;
import java.util.List;

/**
 * Created by Zetas on 2016/7/11.
 */
public class TraceDataStorageMongo implements TraceDataStorage {

    private List<TraceDataHandle> traceDataHandleList;

    public void setTraceDataHandleList(List<TraceDataHandle> traceDataHandleList) {
        this.traceDataHandleList = traceDataHandleList;
    }

    public void addSpan(List<Span> spanList) {
        for (TraceDataHandle traceDataHandle : traceDataHandleList) {
            traceDataHandle.handle(spanList);
        }
    }

}
