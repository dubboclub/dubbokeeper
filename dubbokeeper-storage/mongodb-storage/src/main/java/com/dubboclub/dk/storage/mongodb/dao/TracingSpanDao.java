package com.dubboclub.dk.storage.mongodb.dao;

import com.dubboclub.dk.storage.mongodb.dto.TracingSpanDto;
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

    public void add(TracingSpanDto dto) {
        mongoTemplate.insert(dto, SPAN_COLLECTIONS);
    }

    public void add(List<TracingSpanDto> dtoList) {
        mongoTemplate.insert(dtoList, SPAN_COLLECTIONS);
    }

    public List<TracingSpanDto> findByTraceId(String traceId) {
        return mongoTemplate.find(new Query(Criteria.where("traceId").is(traceId)), TracingSpanDto.class, SPAN_COLLECTIONS);
    }

    public TracingSpanDto findBySpanId(String spanId) {
        return mongoTemplate.findOne(new Query(Criteria.where("spanId").is(spanId)), TracingSpanDto.class, SPAN_COLLECTIONS);
    }
}
