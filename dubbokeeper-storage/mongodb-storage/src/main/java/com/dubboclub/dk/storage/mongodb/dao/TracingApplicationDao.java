package com.dubboclub.dk.storage.mongodb.dao;

import com.dubboclub.dk.storage.model.Application;
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

    public Application findById(Integer id) {
        return mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), Application.class, APPLICATION_COLLECTIONS);
    }

    public List<Application> findAll() {
        return mongoTemplate.findAll(Application.class, APPLICATION_COLLECTIONS);
    }

    public void add(Application application) {
        if (findById(application.getId()) == null) {
            mongoTemplate.save(application, APPLICATION_COLLECTIONS);
        }
    }
}
