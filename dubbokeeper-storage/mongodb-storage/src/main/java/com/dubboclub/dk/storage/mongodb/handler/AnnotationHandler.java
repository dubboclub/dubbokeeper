package com.dubboclub.dk.storage.mongodb.handler;


import com.dubboclub.dk.storage.TraceDataHandler;
import com.dubboclub.dk.storage.model.AnnotationEntity;
import com.dubboclub.dk.storage.mongodb.dao.TracingAnnotationDao;
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
    public void handle(Span span) {
        List<AnnotationEntity> annotations = new LinkedList<AnnotationEntity>();
        for (Annotation annotation : span.getAnnotationList()) {
            annotations.add(handle(span, annotation));
        }
        for (BinaryAnnotation binaryAnnotation : span.getBinaryAnnotationList()) {
            annotations.add(handle(span, binaryAnnotation));
        }
        dao.add(annotations);
    }

    private AnnotationEntity handle(Span span, Annotation annotation) {
        AnnotationEntity entity = new AnnotationEntity();
        entity.setId(Long.valueOf(annotation.hashCode()));
        entity.setKey(annotation.getValue());
        entity.setIp(annotation.getHost().getIp());
        entity.setPort(annotation.getHost().getPort());
        entity.setTimestamp(annotation.getTimestamp());
        entity.setTraceId(span.getTraceId());
        entity.setSpanId(span.getId());
        entity.setServiceId(span.getServiceName().hashCode());
        return entity;
    }

    private AnnotationEntity handle(Span span, BinaryAnnotation annotation) {
        AnnotationEntity entity = new AnnotationEntity();
        entity.setId(Long.valueOf(annotation.hashCode()));
        entity.setKey(annotation.getKey());
        entity.setValue(annotation.getValue());
        entity.setIp(annotation.getHost().getIp());
        entity.setPort(annotation.getHost().getPort());
        entity.setTraceId(span.getTraceId());
        entity.setSpanId(span.getId());
        entity.setServiceId(span.getServiceName().hashCode());
        return entity;
    }
}
