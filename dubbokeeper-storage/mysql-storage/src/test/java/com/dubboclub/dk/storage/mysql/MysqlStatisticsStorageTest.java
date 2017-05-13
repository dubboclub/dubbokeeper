package com.dubboclub.dk.storage.mysql;

import com.dubboclub.dk.storage.model.ApplicationInfo;
import com.dubboclub.dk.storage.model.MethodMonitorOverview;
import com.dubboclub.dk.storage.model.Statistics;
import com.dubboclub.dk.storage.model.StatisticsOverview;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @date: 2015/12/28.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mysql.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
@Ignore
public class MysqlStatisticsStorageTest extends ApplicationStartUp {

    @Autowired
    private MysqlStatisticsStorage mysqlStatisticsStorage;


    private static final String[] SERVICES = {"CardService","UserService","LoginService"};

    @Test
    public void testStoreStatistics() throws Exception {
        Random random = new Random();
        for(int i=0;i<10000;i++){
            Statistics statistics = new Statistics();
            statistics.setApplication("hello"+i%10);
            statistics.setConcurrent(new Long(random.nextInt(100)));
            statistics.setElapsed(Long.valueOf(random.nextInt(100000)));
            statistics.setFailureCount(random.nextInt(10));
            statistics.setHost("192.168.0.1");
            statistics.setInput(Long.valueOf(random.nextInt(400)));
            statistics.setKbps(random.nextInt(500));
            statistics.setMethod("save");
            statistics.setOutput(Long.valueOf(random.nextInt(100)));
            statistics.setRemoteAddress("21.2.2.1");
            statistics.setType(Statistics.ApplicationType.CONSUMER);
            statistics.setTps(random.nextInt(100));
            statistics.setTimestamp(System.currentTimeMillis());
            statistics.setSuccessCount(random.nextInt(5000));
            statistics.setServiceInterface(statistics.getApplication()+"."+SERVICES[random.nextInt(SERVICES.length)]);
            statistics.setRemoteType(Statistics.ApplicationType.PROVIDER);
            mysqlStatisticsStorage.storeStatistics(statistics);
        }
    }

    @Test
    public void testQueryStatisticsForMethod() throws Exception {
        List<Statistics> statisticses =  mysqlStatisticsStorage.queryStatisticsForMethod("hello0", "hello0.CardService", "save",System.currentTimeMillis() - 60 * 60 * 1000, System.currentTimeMillis());
        printObject(statisticses);
    }

    @Test
    public void testQueryMethodMonitorOverview() throws Exception {
        Collection<MethodMonitorOverview> methodMonitorOverviews = mysqlStatisticsStorage.queryMethodMonitorOverview("hello0", "hello0.CardService", 3, System.currentTimeMillis() - 60 * 60 * 1000, System.currentTimeMillis());
        printObject(methodMonitorOverviews);
    }

    @Test
    public void testQueryApplications() throws Exception {

    }

    @Test
    public void testQueryApplicationInfo() throws Exception {
        ApplicationInfo applicationInfo =  mysqlStatisticsStorage.queryApplicationInfo("hello0", System.currentTimeMillis() - 10 * 60 * 1000, System.currentTimeMillis());
        printObject(applicationInfo);
    }

    @Test
    public void testQueryApplicationOverview() throws Exception {
        StatisticsOverview statisticsOverview = mysqlStatisticsStorage.queryApplicationOverview("hello0", System.currentTimeMillis() - 60 * 60 * 1000, System.currentTimeMillis());
        printObject(statisticsOverview);
    }

    @Test
    public void testQueryServiceOverview() throws Exception {
        StatisticsOverview statisticsOverview = mysqlStatisticsStorage.queryServiceOverview("hello0","hello0.CardService",System.currentTimeMillis() - 60 * 60 * 1000, System.currentTimeMillis());
        printObject(statisticsOverview);
    }

    @Test
    public void testQueryServiceByApp() throws Exception {
        Collection ret = mysqlStatisticsStorage.queryServiceByApp("hello0", System.currentTimeMillis() - 10 * 60 * 1000, System.currentTimeMillis());
        printObject(ret);
    }
}
