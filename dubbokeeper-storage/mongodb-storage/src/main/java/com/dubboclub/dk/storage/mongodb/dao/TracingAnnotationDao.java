package com.dubboclub.dk.storage.mongodb.dao;

import com.dubboclub.dk.storage.mongodb.dto.TracingAnnotationDto;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Created by Zetas on 2016/7/11.
 */
public class TracingAnnotationDao {

    private static Logger logger = LoggerFactory.getLogger(TracingAnnotationDao.class);

    private static final String ANNOTATION_COLLECTIONS = "tracing-annotation";

    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void add(TracingAnnotationDto dto) {
        mongoTemplate.insert(dto, ANNOTATION_COLLECTIONS);
    }

    public void add(List<TracingAnnotationDto> dtoList) {
        mongoTemplate.insert(dtoList, ANNOTATION_COLLECTIONS);
    }

    public List<TracingAnnotationDto> findByTraceId(String traceId) {
        return mongoTemplate.find(new Query(Criteria.where("traceId").is(traceId)), TracingAnnotationDto.class, ANNOTATION_COLLECTIONS);
    }

    public List<TracingAnnotationDto> findBySpanId(String spanId) {
        return mongoTemplate.find(new Query(Criteria.where("spanId").is(spanId)), TracingAnnotationDto.class, ANNOTATION_COLLECTIONS);
    }
}
