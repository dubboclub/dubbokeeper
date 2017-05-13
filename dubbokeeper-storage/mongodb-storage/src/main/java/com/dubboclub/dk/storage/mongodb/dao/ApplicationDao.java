package com.dubboclub.dk.storage.mongodb.dao;

import com.dubboclub.dk.storage.model.ApplicationInfo;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Created by hideh on 2016/3/21.
 */
public class ApplicationDao {

    private static final String APPLICATION_COLLECTIONS = "application";

    private MongoTemplate mongoTemplate;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void updateAppType(String application, int type){
        Query query = new Query(
                Criteria.where("applicationName").is(application)
        );
        mongoTemplate.updateMulti(query,new Update().set("applicationType",type),ApplicationInfo.class,APPLICATION_COLLECTIONS);
    }

    public List<ApplicationInfo> findAll(){
        return mongoTemplate.findAll(ApplicationInfo.class,APPLICATION_COLLECTIONS);
    }

    public void addApplication(ApplicationInfo applicationInfo){
        Query query = new Query(
                Criteria.where("applicationName").is(applicationInfo.getApplicationName().toLowerCase())
        );
        boolean result = mongoTemplate.exists(query,ApplicationInfo.class,APPLICATION_COLLECTIONS);
        if(!result){
         mongoTemplate.save(applicationInfo,APPLICATION_COLLECTIONS);
        }
    }


}
