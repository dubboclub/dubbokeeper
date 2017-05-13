package com.dubboclub.dk.storage.mongodb.dao;

import com.dubboclub.dk.storage.model.Trace;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Created by Zetas on 2016/7/11.
 */
public class TracingTraceDao {
    private static Logger logger = LoggerFactory.getLogger(TracingTraceDao.class);

    private static final String TRACE_COLLECTIONS = "tracing-trace";

    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void add(Trace trace) {
        mongoTemplate.insert(trace, TRACE_COLLECTIONS);
    }

    public void add(List<Trace> traces) {
        mongoTemplate.insert(traces, TRACE_COLLECTIONS);
    }

    public List<Trace> findByServiceIdAndDurationBetweenAndTimeBetween(Integer serviceId, Long startTime, Long endTime, Integer minDuration, Integer maxDuration) {
        Criteria criteria = Criteria
                .where("serviceId").is(serviceId)
                .and("duration").gte(minDuration).lte(maxDuration)
                .and("time").gte(startTime).lte(endTime);

        return mongoTemplate.find(new Query(criteria), Trace.class, TRACE_COLLECTIONS);
    }

    public Trace findById(Long id) {
        return mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), Trace.class, TRACE_COLLECTIONS);
    }
}
