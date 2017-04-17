package com.dubboclub.dk.storage.mongodb.handler;


import com.dubboclub.dk.storage.TraceDataHandler;
import com.dubboclub.dk.storage.mongodb.dao.TracingSpanDao;
import com.dubboclub.dk.tracing.api.Annotation;
import com.dubboclub.dk.tracing.api.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Zetas on 2016/7/11.
 */
public class SpanHandler implements TraceDataHandler {

    private static Logger logger = LoggerFactory.getLogger(SpanHandler.class);

    private TracingSpanDao dao;

    public void setDao(TracingSpanDao dao) {
        this.dao = dao;
    }

    @Override
    public void handle(Span span) {
        if (isConsumerSideSpan(span)) {
            dao.add(span);
        }
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
