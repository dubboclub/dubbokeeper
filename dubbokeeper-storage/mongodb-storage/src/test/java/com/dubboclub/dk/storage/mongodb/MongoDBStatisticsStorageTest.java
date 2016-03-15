package com.dubboclub.dk.storage.mongodb;

import com.dubboclub.dk.storage.StatisticsStorage;
import com.dubboclub.dk.storage.model.*;
import com.dubboclub.dk.storage.mongodb.dto.TempMethodOveride;
import com.dubboclub.dk.storage.mongodb.dto.TempServiceOveride;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang.time.DateUtils;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by HideHai on 2016/3/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/META-INF/spring/mongodb.xml" })
public class MongoDBStatisticsStorageTest {

    Logger LOGGER = LoggerFactory.getLogger("dubbokeeper-server");

    @Autowired
    private StatisticsStorage statisticsStorage;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String APPLICATION_COLLECTIONS = "application";

    private static final String STATISTICS_COLLECTIONS = "statistics";

    /**
     *
     */
    @Test
    public void storeStatisticsTest() {
        Statistics statistics = new Statistics();
        statistics.setApplication("test_hh_service");
        statistics.setConcurrent(2);
        statistics.setElapsed(1);
        statistics.setFailureCount(0);
        statistics.setHost("10.100.152.111");
        statistics.setInput(100);
        statistics.setOutput(200);
        statistics.setKbps(300);
        statistics.setMethod("fetchData");
        statistics.setRemoteAddress("10.100.152.200");
        statistics.setRemoteType(Statistics.ApplicationType.PROVIDER);
        statistics.setServiceInterface("com.hidehai.dubbo.MMSerivce");
        statistics.setTimestamp(new Date().getTime());
        statistics.setTps(150);

        statisticsStorage.storeStatistics(statistics);
    }

    @Test
    public void addApplicationTest(){
        String application = "test_zz_service";
        String colName = "application";
        int type =1;
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setApplicationName(application);
        applicationInfo.setApplicationType(1);
        mongoTemplate.save(applicationInfo,colName);
    }

    @Test
    public void queryStatisticsForMethodTest(){
        String appName = "test_hh_service";
        String interfaceName = "com.hidehai.dubbo.HHSerivce";
        String methodName = "fetchData";

        List<Statistics> statisticsList = statisticsStorage.queryStatisticsForMethod(appName,interfaceName,methodName,
                DateUtils.addHours(new Date(),-5).getTime(),new Date().getTime());
        if(statisticsList != null){
            for(Statistics s :statisticsList){
                LOGGER.info(s.getServiceInterface()+" "+s.getHost());
            }
        }
    }

    @Test
    public void findMethodForServiceTest(){
        String serviceInterface = "com.hidehai.dubbo.HHSerivce";
        String colName = "statistics_test_hh_service";

        //Aggregation条件之间存在顺序关系
        TypedAggregation aggregation =new TypedAggregation(Statistics.class,
                Aggregation.project("method","serviceInterface"),               //限制结果集包含域
                Aggregation.match(Criteria.where("serviceInterface").is(serviceInterface)),    //过滤数据
                Aggregation.group("method").first("method").as("m").count().as("total"), //分组聚合
                Aggregation.sort(Sort.Direction.DESC,"total")   //数据排序
        );

        List<Temp> statisticses = mongoTemplate.aggregate(aggregation,colName,Temp.class).getMappedResults();
        LOGGER.info(statisticses.size()+"");
        for (Temp s : statisticses){
            LOGGER.info(String.format("method: %s ; count: %s",s.getM(),s.getTotal()));
        }
    }

    @Test
    public void findMethodMaxItemByServiceTest(){
        String serviceInterface = "com.hidehai.dubbo.HHSerivce";
        String colName = "concurrent";
        String method = "fetchData2";
        String application = "test_hh_service";

        Query query = new Query(
                Criteria.where("serviceInterface").is(serviceInterface)
        );
        query.addCriteria(Criteria.where("method").is(method));
        query.addCriteria(Criteria.where("timestamp").gte(DateUtils.addHours(new Date(),-5).getTime()).lte(new Date().getTime()));
        query.with(new Sort(Sort.Direction.DESC,colName)).limit(1);

        Statistics statisticses = mongoTemplate.findOne(query,Statistics.class,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()));
        if(statisticses != null){
            LOGGER.info(statisticses.getConcurrent()+"");
        }
    }

    @Test
    public void queryMethodMonitorOverviewTest(){
        String serviceInterface = "com.hidehai.dubbo.HHSerivce";
        String application = "test_hh_service";
        Collection<MethodMonitorOverview> overviews = statisticsStorage.queryMethodMonitorOverview(application,
                serviceInterface,5,DateUtils.addHours(new Date(),-5).getTime(),new Date().getTime());

        for(MethodMonitorOverview m : overviews){
            LOGGER.info(
                    String.format("method: %s - Concurrent:%s - kbps:%s",m.getMethod(),m.getMaxConcurrent(),m.getMaxKbps()));
        }
    }

    @Test
    public void queryApplicationsTest(){
        Collection<ApplicationInfo> infos = statisticsStorage.queryApplications();
        for(ApplicationInfo info : infos){
           LOGGER.info(String.format("appName : %s",info.getApplicationName()));
        }
    }

    @Test
    public void queryApplicationOverviewTest(){
        String application = "test_hh_service";
        Date sdate = DateUtils.addDays(new Date(),-1);
        Date ldate = new Date();


        StatisticsOverview statisticsOverview = statisticsStorage.queryApplicationOverview(application,sdate.getTime(),ldate.getTime());
        Assert.assertNotNull(statisticsOverview);
        Assert.assertNotNull(statisticsOverview.getConcurrentItems());
        Assert.assertNotNull(statisticsOverview.getElapsedItems());
        Assert.assertNotNull(statisticsOverview.getFaultItems());
        Assert.assertNotNull(statisticsOverview.getSuccessItems());
    }


    @Test
    public  void findApplicationOverviewTest(){
        Date sdate = DateUtils.addDays(new Date(),-1);
        Date ldate = new Date();
        String application = "test_hh_service";

        Query query = new Query();
        query.addCriteria(Criteria.where("timestamp").gte(sdate.getTime()).lte(ldate.getTime()));
        query.with(new Sort(Sort.Direction.DESC,"concurrent")).limit(200);

        List<Statistics>  statisticses = mongoTemplate.find(query,Statistics.class,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()));

        for(Statistics s : statisticses){
            LOGGER.info(s.getConcurrent()+"");
        }
    }

    @Test
    public void queryServiceOverviewTest(){
        String application = "test_hh_service";
        String serviceInterface ="com.hidehai.dubbo.HHSerivce";
        Date sdate = DateUtils.addDays(new Date(),-1);
        Date ldate = new Date();

        StatisticsOverview statisticsOverview = statisticsStorage.queryServiceOverview(application,serviceInterface,sdate.getTime(),ldate.getTime());
        Assert.assertNotNull(statisticsOverview);
        Assert.assertNotNull(statisticsOverview.getConcurrentItems());
        Assert.assertNotNull(statisticsOverview.getElapsedItems());
        Assert.assertNotNull(statisticsOverview.getFaultItems());
        Assert.assertNotNull(statisticsOverview.getSuccessItems());
    }

    @Test
    public void findServiceByAppTest(){
        String application = "test_hh_service";


        TypedAggregation aggregation =new TypedAggregation(Statistics.class,
                Aggregation.project("serviceInterface","remoteType").and("serviceInterface").as("name"),
                Aggregation.group(Aggregation.fields("name").and("remoteType")).count().as("totalNum")
        );

        AggregationResults<BasicDBObject> result = mongoTemplate.aggregate(aggregation,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()),
                BasicDBObject.class);
        System.out.println(aggregation.toString());
        System.out.println(result.getMappedResults());

//        AggregationResults<ServiceInfo> result = mongoTemplate.aggregate(aggregation,
//                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()),
//                ServiceInfo.class);
//
//        for(ServiceInfo s : result){
//            LOGGER.info(s.getName());
//        }

    }

    class Temp{
        String m;
        int total;

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
