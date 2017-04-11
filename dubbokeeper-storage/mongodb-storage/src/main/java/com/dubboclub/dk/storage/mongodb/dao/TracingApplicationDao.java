package com.dubboclub.dk.storage.mongodb.dao;

import com.dubboclub.dk.storage.mongodb.dto.TracingApplicationDto;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Created by Zetas on 2016/7/11.
 */
public class TracingApplicationDao {

    private static Logger logger = LoggerFactory.getLogger(ApplicationDao.class);

    private static final String APPLICATION_COLLECTIONS = "tracing-application";

    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public TracingApplicationDto findOneByApplicationId(Integer applicationId) {
        return mongoTemplate.findOne(new Query(Criteria.where("applicationId").is(applicationId)), TracingApplicationDto.class, APPLICATION_COLLECTIONS);
    }

    public List<TracingApplicationDto> findAll() {
        return mongoTemplate.findAll(TracingApplicationDto.class, APPLICATION_COLLECTIONS);
    }

    public void add(TracingApplicationDto dto) {
        if (findOneByApplicationId(dto.getApplicationId()) == null) {
            mongoTemplate.save(dto, APPLICATION_COLLECTIONS);
        }
    }
}
