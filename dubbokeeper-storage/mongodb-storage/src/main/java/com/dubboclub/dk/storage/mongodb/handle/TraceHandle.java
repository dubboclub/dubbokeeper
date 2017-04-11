package com.dubboclub.dk.storage.mongodb.handle;

import com.dubboclub.dk.storage.mongodb.TraceDataHandle;
import com.dubboclub.dk.storage.mongodb.dao.TracingTraceDao;
import com.dubboclub.dk.storage.mongodb.dto.TracingTraceDto;
import com.dubboclub.dk.tracing.api.Annotation;
import com.dubboclub.dk.tracing.api.Span;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Zetas on 2016/7/11.
 */
public class TraceHandle implements TraceDataHandle {

    private static Logger logger = LoggerFactory.getLogger(TraceHandle.class);

    private TracingTraceDao dao;

    public void setDao(TracingTraceDao dao) {
        this.dao = dao;
    }

    @Override
    public void handle(List<Span> spanList) {
        logger.debug("span list size: {}", spanList.size());
        List<TracingTraceDto> dtoList = new LinkedList<TracingTraceDto>();
        for (Span span : spanList) {
            if (isRoot(span)) {
                dtoList.add(handle(span));
            }
        }
        dao.add(dtoList);
    }

    public TracingTraceDto handle(Span span) {
        long startTime = startTime(span);
        long endTime = endTime(span);
        TracingTraceDto dto = new TracingTraceDto();
        dto.setTraceId(span.getTraceId());
        dto.setDuration((int) (endTime - startTime));
        dto.setTime(endTime);
        dto.setServiceId(span.getServiceName().hashCode());
        return dto;
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
