package com.dubboclub.dk.storage.mongodb.dao;

import com.dubboclub.dk.storage.mongodb.dto.TracingServiceDto;
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

    public List<TracingServiceDto> findAll() {
        return mongoTemplate.findAll(TracingServiceDto.class, SERVICE_COLLECTIONS);
    }

    public TracingServiceDto findOneByServiceId(Integer serviceId) {
        return mongoTemplate.findOne(new Query(Criteria.where("serviceId").is(serviceId)), TracingServiceDto.class, SERVICE_COLLECTIONS);
    }

    public void add(TracingServiceDto dto) {
        if (findOneByServiceId(dto.getServiceId()) == null) {
            mongoTemplate.save(dto, SERVICE_COLLECTIONS);
        }
    }
}
