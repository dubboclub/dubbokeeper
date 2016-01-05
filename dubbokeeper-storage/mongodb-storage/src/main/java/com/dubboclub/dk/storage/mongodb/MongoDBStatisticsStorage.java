package com.dubboclub.dk.storage.mongodb;

import com.alibaba.dubbo.common.extension.ExtensionFactory;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.dubboclub.dk.storage.StatisticsStorage;
import com.dubboclub.dk.storage.model.*;
import com.mongodb.Function;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.List;

/**
 * @date: 2015/12/14.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mongodb.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public class MongoDBStatisticsStorage implements StatisticsStorage {


    private volatile static MongoDatabase mongoDatabase;

    private MongoTemplate mongoTemplate;

    private static final String APPLICATION_COLLECTIONS = "applications";

    private static final String STATISTICS_COLLECTIONS = "statistics";



    @Override
    public void storeStatistics(Statistics statistics) {

    }

    @Override
    public List<Statistics> queryStatisticsForMethod(String application, String serviceInterface, String method, long startTime, long endTime) {
        return null;
    }

    @Override
    public Collection<MethodMonitorOverview> queryMethodMonitorOverview(String application, String serviceInterface, int methodSize, long startTime, long endTime) {
        return null;
    }

    @Override
    public Collection<ApplicationInfo> queryApplications() {
        return null;
    }

    @Override
    public ApplicationInfo queryApplicationInfo(String application, long start, long end) {
        return null;
    }

    @Override
    public StatisticsOverview queryApplicationOverview(String application, long start, long end) {
        return null;
    }

    @Override
    public StatisticsOverview queryServiceOverview(String application, String service, long start, long end) {
        return null;
    }

    @Override
    public Collection<ServiceInfo> queryServiceByApp(String application, long start, long end) {
        return null;
    }
}
