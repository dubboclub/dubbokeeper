package com.dubboclub.dk.storage.mongodb.dao;

import com.dubboclub.dk.storage.model.ServiceInfo;
import com.dubboclub.dk.storage.model.Statistics;
import com.dubboclub.dk.storage.mongodb.dto.TempMethodOveride;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Created by hideh on 2016/3/21.
 */
public class StatisticsDao {

    private static final String STATISTICS_COLLECTIONS = "statistics";

    private MongoTemplate mongoTemplate;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void addOne(String application,Statistics statistics){
        mongoTemplate.save(statistics,String.format("%s_%s",STATISTICS_COLLECTIONS,application));
    }


    public void batchInsert(String application,List<Statistics> statisticsList){
        mongoTemplate.insert(statisticsList,String.format("%s_%s",STATISTICS_COLLECTIONS,application));
    }

    public List<Statistics> queryStatisticsForMethod(String application, String serviceInterface, String method, long startTime, long endTime){
        Query query = new Query(
                Criteria.where("serviceInterface").is(serviceInterface)
        );
        query.addCriteria(Criteria.where("method").is(method));
        query.addCriteria(Criteria.where("timestamp").gte(startTime).lte(endTime));

        String collectionName = String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase());
        List<Statistics>  statisticses = mongoTemplate.find(query,Statistics.class,collectionName);
        return statisticses;
    }

    /**
     * 查询接口下的方法
     * @param application
     * @param serviceInterface
     * @return
     */
    public List<TempMethodOveride> findMethodForService(String application, String serviceInterface){
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
     *  查询应用接口区间倒排数据
     * @param column
     * @param application
     * @param serviceInterface
     * @param method
     * @param startTime
     * @param endTime
     */
    public Statistics findMethodMaxItemByService(String column,String application, String serviceInterface, String method, long startTime, long endTime){
        Query query = new Query(
                Criteria.where("serviceInterface").is(serviceInterface)
        );
        query.addCriteria(Criteria.where("method").is(method));
        query.addCriteria(Criteria.where("timestamp").gte(startTime).lte(endTime));
        query.with(new Sort(Sort.Direction.DESC,column)).limit(1);

        Statistics statistics = mongoTemplate.findOne(query,Statistics.class,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()));
        return statistics;
    }



    /**
     *  查询应用概要信息
     * @param application
     * @param startTime
     * @param item
     * @param endTime
     * @return
     */
    public List<Statistics> findApplicationOverview(String application,String item,long startTime, long endTime){
        Query query = new Query();
        query.addCriteria(Criteria.where("timestamp").gte(startTime).lte(endTime));
        query.with(new Sort(Sort.Direction.DESC,item)).limit(200);

        List<Statistics>  statisticses = mongoTemplate.find(query,Statistics.class,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()));
        return statisticses;
    }


    /**
     *  查询接口概要信息
     * @param application
     * @param startTime
     * @param item
     * @param service
     * @param endTime
     * @return
     */
    public List<Statistics> findServiceOverview(String application,String service,String item,long startTime, long endTime){
        Query query = new Query();
        query.addCriteria(Criteria.where("serviceInterface").is(service));
        query.addCriteria(Criteria.where("timestamp").gte(startTime).lte(endTime));
        query.with(new Sort(Sort.Direction.DESC,item)).limit(200);

        List<Statistics>  statisticses = mongoTemplate.find(query,Statistics.class,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()));
        return statisticses;
    }


    /**
     * 查询服务接口
     * @param application
     * @return
     */
    public List<ServiceInfo> findServiceByApp(String application){
        TypedAggregation aggregation =new TypedAggregation(Statistics.class,
                Aggregation.project("remoteType","serviceInterface"),
                Aggregation.group("serviceInterface","remoteType"),
                Aggregation.project("remoteType").and("serviceInterface").as("name")
        );

        List<ServiceInfo> serviceInfos = mongoTemplate.aggregate(aggregation,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()),
                ServiceInfo.class).getMappedResults();
        return  serviceInfos;
    }



    /**
     * 查询时间区间内的item最大值
     * @param application
     * @param service
     * @param startTime
     * @param endTime
     * @return
     */
    public Statistics queryMaxItemByService(String application,String service,String item,long startTime,long endTime){
        Query query = new Query();
        if(service != null){
            query.addCriteria(Criteria.where("serviceInterface").is(service));
        }
        query.addCriteria(Criteria.where("timestamp").gte(startTime).lte(endTime));
        query.with(new Sort(Sort.Direction.DESC,item));

        Statistics statistics = mongoTemplate.findOne(query,Statistics.class,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()));
        return statistics;
    }
}
