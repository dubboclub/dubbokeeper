package com.dubboclub.dk.storage.mongodb.handler;


import com.dubboclub.dk.storage.TraceDataHandler;
import com.dubboclub.dk.storage.mongodb.dao.TracingAnnotationDao;
import com.dubboclub.dk.storage.mongodb.dto.TracingAnnotationDto;
import com.dubboclub.dk.tracing.api.Annotation;
import com.dubboclub.dk.tracing.api.BinaryAnnotation;
import com.dubboclub.dk.tracing.api.Span;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Zetas on 2016/7/11.
 */
public class AnnotationHandler implements TraceDataHandler {

    private static Logger logger = LoggerFactory.getLogger(AnnotationHandler.class);

    private TracingAnnotationDao dao;

    public void setDao(TracingAnnotationDao dao) {
        this.dao = dao;
    }

    @Override
    public void handle(List<Span> spanList) {
        logger.debug("span list size: {}", spanList.size());
        List<TracingAnnotationDto> dtoList = new LinkedList<TracingAnnotationDto>();
        for (Span span : spanList) {
            for (Annotation annotation : span.getAnnotationList()) {
                dtoList.add(handle(span, annotation));
            }
            for (BinaryAnnotation binaryAnnotation : span.getBinaryAnnotationList()) {
                dtoList.add(handle(span, binaryAnnotation));
            }
        }
        dao.add(dtoList);
    }

    private TracingAnnotationDto handle(Span span, Annotation annotation) {
        TracingAnnotationDto dto = new TracingAnnotationDto();
        dto.setKey(annotation.getValue());
        dto.setId(annotation.getHost().getIp());
        dto.setPort(annotation.getHost().getPort());
        dto.setTime(annotation.getTimestamp());
        dto.setTraceId(span.getTraceId());
        dto.setSpanId(span.getId());
        dto.setServiceId(span.getServiceName().hashCode());
        return dto;
    }

    private TracingAnnotationDto handle(Span span, BinaryAnnotation annotation) {
        TracingAnnotationDto dto = new TracingAnnotationDto();
        dto.setKey(annotation.getKey());
        dto.setValue(annotation.getValue());
        dto.setId(annotation.getHost().getIp());
        dto.setPort(annotation.getHost().getPort());
        dto.setTraceId(span.getTraceId());
        dto.setSpanId(span.getId());
        dto.setServiceId(span.getServiceName().hashCode());
        return dto;
    }
}
