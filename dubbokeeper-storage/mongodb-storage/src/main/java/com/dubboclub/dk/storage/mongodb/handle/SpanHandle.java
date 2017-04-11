package com.dubboclub.dk.storage.mongodb.handle;


import com.dubboclub.dk.storage.mongodb.TraceDataHandle;
import com.dubboclub.dk.storage.mongodb.dao.TracingSpanDao;
import com.dubboclub.dk.storage.mongodb.dto.TracingSpanDto;
import com.dubboclub.dk.tracing.api.Annotation;
import com.dubboclub.dk.tracing.api.Span;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Zetas on 2016/7/11.
 */
public class SpanHandle implements TraceDataHandle {

    private static Logger logger = LoggerFactory.getLogger(SpanHandle.class);

    private TracingSpanDao dao;

    public void setDao(TracingSpanDao dao) {
        this.dao = dao;
    }

    @Override
    public void handle(List<Span> spanList) {
        logger.debug("span list size: {}", spanList.size());
        List<TracingSpanDto> dtoList = new LinkedList<TracingSpanDto>();
        for (Span span : spanList) {
            if (isConsumerSideSpan(span)) {
                dtoList.add(handle(span));
            }
        }
        dao.add(dtoList);
    }

    private TracingSpanDto handle(Span span) {
        TracingSpanDto dto = new TracingSpanDto();
        dto.setSpanId(span.getId());
        dto.setParentId(span.getParentId());
        dto.setTraceId(span.getTraceId());
        dto.setName(span.getName());
        dto.setServiceId(span.getServiceName().hashCode());
        return dto;
    }

    private boolean isConsumerSideSpan(Span span) {
        boolean isConsumerSide = false;
        for (Annotation annotation : span.getAnnotationList()) {
            if (Annotation.CLIENT_SEND.equals(annotation.getValue())) {
                isConsumerSide = true;
                break;
            }
        }
        return isConsumerSide;
    }

}
