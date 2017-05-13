package com.dubboclub.dk.storage.mongodb.handler;

import com.dubboclub.dk.storage.TraceDataHandler;
import com.dubboclub.dk.storage.model.Trace;
import com.dubboclub.dk.storage.mongodb.dao.TracingTraceDao;
import com.dubboclub.dk.tracing.api.Annotation;
import com.dubboclub.dk.tracing.api.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Zetas on 2016/7/11.
 */
public class TraceHandler implements TraceDataHandler {

    private static Logger logger = LoggerFactory.getLogger(TraceHandler.class);

    private TracingTraceDao dao;

    public void setDao(TracingTraceDao dao) {
        this.dao = dao;
    }

    @Override
    public void handle(Span span) {
        if (isRoot(span)) {
            long startTime = startTime(span);
            long endTime = endTime(span);

            Trace trace = new Trace();
            trace.setId(span.getTraceId());
            trace.setDuration((int) (endTime - startTime));
            trace.setTimestamp(endTime);
            trace.setServiceId(span.getServiceName().hashCode());

            dao.add(trace);
        }
    }

    private boolean isRoot(Span span) {
        boolean result = false;
        if (span.getParentId() == null) {
            for (Annotation annotation : span.getAnnotationList()) {
                if (Annotation.CLIENT_SEND.equalsIgnoreCase(annotation.getValue())) {
                    result = true;
                }
            }
        }
        return result;
    }

    private long startTime(Span span) {
        long startTime = 0;
        for (Annotation annotation : span.getAnnotationList()) {
            if (Annotation.CLIENT_SEND.equals(annotation.getValue())) {
                startTime = annotation.getTimestamp();
                break;
            }
        }
        return startTime;
    }

    private long endTime(Span span) {
        long endTime = 0;
        for (Annotation annotation : span.getAnnotationList()) {
            if (Annotation.CLIENT_RECEIVE.equals(annotation.getValue())) {
                endTime = annotation.getTimestamp();
                break;
            }
        }
        return endTime;
    }

}
