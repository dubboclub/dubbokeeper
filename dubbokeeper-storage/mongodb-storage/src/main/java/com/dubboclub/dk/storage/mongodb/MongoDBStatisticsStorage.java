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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
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

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String APPLICATION_COLLECTIONS = "applications";

    private static final String STATISTICS_COLLECTIONS = "statistics";



    @Override
    public void storeStatistics(Statistics statistics) {
        LOGGER.info(statistics.getApplication());
        String app = statistics.getApplication();
        if(app == null){
            LOGGER.warn("url.application miss!");
            return;
        }
        String collectionName = String.format("%s_%s",STATISTICS_COLLECTIONS,app.toLowerCase());
        mongoTemplate.save(statistics,collectionName);
    }

    @Override
    public List<Statistics> queryStatisticsForMethod(String application, String serviceInterface, String method, long startTime, long endTime) {
       Query query = new Query(
         Criteria.where("serviceInterface").is(serviceInterface)
       );
        query.addCriteria(Criteria.where("method").is(method));
        query.addCriteria(Criteria.where("timestamp").gte(startTime).lte(endTime));

        String collectionName = String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase());
        List<Statistics>  statisticses = mongoTemplate.find(query,Statistics.class,collectionName);
        return statisticses;
    }

    @Override
    public Collection<MethodMonitorOverview> queryMethodMonitorOverview(String application, String serviceInterface, int methodSize, long startTime, long endTime) {
        List<TempMethodOveride> methods = findMethodForService(application,serviceInterface);

        Collection<MethodMonitorOverview> methodMonitorOverviews = new ArrayList<MethodMonitorOverview>(methods.size());
        for(TempMethodOveride method : methods){
            MethodMonitorOverview methodMonitorOverview = new MethodMonitorOverview();
            Statistics concurrent= findMethodMaxItemByService("concurrent",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics elapsed= findMethodMaxItemByService("elapsed",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics failure= findMethodMaxItemByService("failureCount",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics input= findMethodMaxItemByService("input",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics kbps= findMethodMaxItemByService("kbps",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics output= findMethodMaxItemByService("output",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics success= findMethodMaxItemByService("successCount",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics tps= findMethodMaxItemByService("tps",application,serviceInterface,method.getM(),startTime,endTime);

            methodMonitorOverview.setMaxConcurrent(concurrent==null?0:concurrent.getConcurrent());
            methodMonitorOverview.setMaxElapsed(elapsed==null?0:elapsed.getElapsed());
            methodMonitorOverview.setMaxFailure(failure==null?0:failure.getFailureCount());
            methodMonitorOverview.setMaxInput(input==null?0:input.getInput());
            methodMonitorOverview.setMaxKbps(kbps==null?0:kbps.getKbps());
            methodMonitorOverview.setMaxOutput(output==null?0:output.getOutput());
            methodMonitorOverview.setMaxSuccess(success==null?0:success.getSuccessCount());
            methodMonitorOverview.setMaxTps(tps==null?0:tps.getTps());
            methodMonitorOverview.setMethod(method.getM());
            methodMonitorOverviews.add(methodMonitorOverview);
        }
        return methodMonitorOverviews;
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

    /**
     * 查询接口下的方法
     * @param application
     * @param serviceInterface
     * @return
     */
    private List<TempMethodOveride> findMethodForService(String application,String serviceInterface){
        TypedAggregation aggregation =new TypedAggregation(Statistics.class,
                Aggregation.project("method","serviceInterface"),               //限制结果集包含域
                Aggregation.match(Criteria.where("serviceInterface").is(serviceInterface)),    //过滤数据
                Aggregation.group("method").first("method").as("m").count().as("total"), //分组聚合
                Aggregation.sort(Sort.Direction.DESC,"total")   //数据排序
        );

        List<TempMethodOveride> methodOverides = mongoTemplate.aggregate(aggregation,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()),
                TempMethodOveride.class).getMappedResults();
        return  methodOverides;
    }


    /**
     * 查询区间倒排数据
     * @param column
     * @param application
     * @param serviceInterface
     * @param method
     * @param startTime
     * @param endTime
     */
    private Statistics findMethodMaxItemByService(String column,String application, String serviceInterface, String method, long startTime, long endTime){
        String sql = "select ${item} from `statistics_${application}` where timestamp>=#{start} \n" +
                "and timestamp<#{end} and serviceInterface=#{serviceInterface} and method=#{method}\n" +
                "order by ${item} desc limit 1;";

        Query query = new Query(
                Criteria.where("serviceInterface").is(serviceInterface)
        );
        query.addCriteria(Criteria.where("method").is(method));
        query.addCriteria(Criteria.where("timestamp").gte(startTime).lte(endTime));
        query.with(new Sort(Sort.Direction.DESC,column)).limit(1);

        Statistics statisticses = mongoTemplate.findOne(query,Statistics.class,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()));

        return statisticses;
    }

    private class TempMethodOveride{
       private  String m;
        private int total;

        public String getM() {
            return m;
        }

        public void setM(String m) {
            this.m = m;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
