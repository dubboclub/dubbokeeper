package com.dubboclub.dk.storage.mongodb.dao;

import com.dubboclub.dk.tracing.api.Span;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Created by Zetas on 2016/7/11.
 */
public class TracingSpanDao {

    private static Logger logger = LoggerFactory.getLogger(TracingSpanDao.class);

    private static final String SPAN_COLLECTIONS = "tracing-span";

    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void add(Span span) {
        mongoTemplate.insert(span, SPAN_COLLECTIONS);
    }

    public void add(List<Span> spans) {
        mongoTemplate.insert(spans, SPAN_COLLECTIONS);
    }

    public List<Span> findByTraceId(String traceId) {
        return mongoTemplate .find(new Query(Criteria.where("traceId").is(traceId)), Span.class, SPAN_COLLECTIONS);
    }

    public Span findById(String spanId) {
        return mongoTemplate .findOne(new Query(Criteria.where("id").is(spanId)), Span.class, SPAN_COLLECTIONS);
    }
}
