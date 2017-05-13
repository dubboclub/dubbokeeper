package com.dubboclub.dk.storage.mongodb.dao;

import com.dubboclub.dk.storage.model.AnnotationEntity;
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

    public void add(AnnotationEntity annotation) { mongoTemplate.insert(annotation, ANNOTATION_COLLECTIONS);
    }

    public void add(List<AnnotationEntity> annotations) {
        mongoTemplate.insert(annotations, ANNOTATION_COLLECTIONS);
    }

    public List<AnnotationEntity> findByTraceId(String traceId) {
        return mongoTemplate.find(new Query(Criteria.where("traceId").is(traceId)), AnnotationEntity.class, ANNOTATION_COLLECTIONS);
    }

    public List<AnnotationEntity> findBySpanId(String spanId) {
        return mongoTemplate.find(new Query(Criteria.where("spanId").is(spanId)), AnnotationEntity.class, ANNOTATION_COLLECTIONS);
    }
}
