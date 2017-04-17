package com.dubboclub.dk.storage.mongodb.dao;

import com.dubboclub.dk.storage.model.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Created by Zetas on 2016/7/11.
 */
public class TracingServiceDao {

    private static Logger logger = LoggerFactory.getLogger(TracingServiceDao.class);

    private static final String SERVICE_COLLECTIONS = "tracing-service";

    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Service> findAll() {
        return mongoTemplate.findAll(Service.class, SERVICE_COLLECTIONS);
    }

    public Service findId(Integer id) {
        return mongoTemplate .findOne(new Query(Criteria.where("id").is(id)), Service.class, SERVICE_COLLECTIONS);
    }

    public void add(Service service) {
        if (findId(service.getId()) == null) {
            mongoTemplate.save(service, SERVICE_COLLECTIONS);
        }
    }
}
