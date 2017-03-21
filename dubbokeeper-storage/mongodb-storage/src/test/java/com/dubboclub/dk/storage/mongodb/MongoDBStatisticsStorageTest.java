package com.dubboclub.dk.storage.mongodb;

import com.dubboclub.dk.storage.StatisticsStorage;
import com.dubboclub.dk.storage.model.ApplicationInfo;
import com.dubboclub.dk.storage.model.MethodMonitorOverview;
import com.dubboclub.dk.storage.model.ServiceInfo;
import com.dubboclub.dk.storage.model.Statistics;
import com.dubboclub.dk.storage.model.StatisticsOverview;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Ignore;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by HideHai on 2016/3/1.
 */
@Ignore
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
    public void storeStatisticsTest() throws InterruptedException {
        Statistics statistics = new Statistics();
        statistics.setApplication("test_aa_service");
        statistics.setConcurrent(Long.valueOf(6));
        statistics.setElapsed(Long.valueOf(1));
        statistics.setFailureCount(5);
        statistics.setSuccessCount(3);
        statistics.setHost("10.100.152.111");
        statistics.setInput(Long.valueOf(100));
        statistics.setOutput(Long.valueOf(200));
        statistics.setKbps(300);
        statistics.setMethod("fetchData");
        statistics.setRemoteAddress("10.100.152.200");
        statistics.setType(Statistics.ApplicationType.PROVIDER);
        statistics.setRemoteType(Statistics.ApplicationType.CONSUMER);
        statistics.setServiceInterface("com.hidehai.dubbo.KKSerivce");
        statistics.setTimestamp(new Date().getTime());
        statistics.setTps(150);

        statisticsStorage.storeStatistics(statistics);
        Thread.sleep(5000);
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
        List<Object>  statisticses = mongoTemplate.findAll(Object.class,"test2");
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
    public void queryApplicationInfoTest(){
        String application = "pms_provider";
        long s = DateUtils.addDays(new Date(),2).getTime();
        long e = new Date().getTime();
        Query query = new Query();
        query.addCriteria(Criteria.where("timestamp").gte(s).lte(e));
        query.with(new Sort(Sort.Direction.DESC,"concurrent"));
        LOGGER.info(query.toString());
        Statistics statistics = mongoTemplate.findOne(query,Statistics.class,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()));
        if(statistics != null){
            LOGGER.info(statistics.getConcurrent()+"");
        }
    }

    @Test
    public void findServiceByAppTest(){
        String application = "test_hh_service";
        TypedAggregation aggregation =new TypedAggregation(Statistics.class,
                Aggregation.project("remoteType","serviceInterface"),
                Aggregation.group("serviceInterface","remoteType").count().as("totalNum"),
                Aggregation.project("remoteType","totalNum").and("serviceInterface").as("name")
        );
//        AggregationResults<ServiceInfo> result = mongoTemplate.aggregate(aggregation,
//                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()),
//                ServiceInfo.class);
//        System.out.println(aggregation.toString());
//        System.out.println(result.getMappedResults());
        AggregationResults<ServiceInfo> result = mongoTemplate.aggregate(aggregation,
                String.format("%s_%s",STATISTICS_COLLECTIONS,application.toLowerCase()),
                ServiceInfo.class);
        for(ServiceInfo s : result){
            LOGGER.info(s.getName());
        }
    }


    @Test
    public void queryServiceByAppTest(){
        String application = "ec_core_consumer";
        Date sdate = DateUtils.addDays(new Date(),-3);
        Date ldate = new Date();
        Collection<ServiceInfo> infos = statisticsStorage.queryServiceByApp(application,sdate.getTime(),ldate.getTime());
        Assert.assertNotNull(infos);
        for(ServiceInfo i : infos){
            Assert.assertNotNull(i.getName());
            Assert.assertNotNull(i.getRemoteType());
            Assert.assertNotNull(i.getMaxConcurrent());
            Assert.assertNotNull(i.getMaxElapsed());
            Assert.assertNotNull(i.getMaxFault());
            Assert.assertNotNull(i.getMaxSuccess());

            LOGGER.info(String.format("server:%s - type:%s - concurent:%s - elapsed:%s - fault:%s - suc:%s",
                    i.getName(),i.getRemoteType(),i.getMaxConcurrent(),i.getMaxElapsed(),i.getMaxFault(),i.getMaxSuccess()));
        }



    }

    @Test
    public void updateAppType(){
        String application = "test_kk_service";
        int type =1;

        Query query = new Query(
                Criteria.where("applicationName").is(application)
        );
        mongoTemplate.updateMulti(query,new Update().set("applicationType",type),ApplicationInfo.class,APPLICATION_COLLECTIONS);
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
