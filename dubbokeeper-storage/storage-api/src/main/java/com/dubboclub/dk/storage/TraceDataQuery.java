package com.dubboclub.dk.storage;


import com.dubboclub.dk.storage.model.*;

import java.util.List;

/**
 * Created by Zetas on 2016/7/14.
 */
public interface TraceDataQuery {
    public List<Application> findAllApplication();

    public List<Service> findAllService();

    public List<Trace> findTrace(Integer serviceId, Long startTime, Long endTime, Integer minDuration, Integer maxDuration);

    public Span findSpanBySpanId(String spanId);

    public List<Span> findSpanByTraceId(String traceId);

    public List<Annotation> findAnnotationBySpanId(String spanId);

    public List<Annotation> findAnnotationByTraceId(String traceId);

}
