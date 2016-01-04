package com.dubboclub.dk.storage.mysql;

import com.dubboclub.dk.storage.model.Statistics;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @date: 2015/12/28.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mysql.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public class MysqlStatisticsStorageTest extends ApplicationStartUp {

    @Autowired
    private MysqlStatisticsStorage mysqlStatisticsStorage;

    @Test
    public void testStoreStatistics() throws Exception {
        for(int i=0;i<10000;i++){
            Statistics statistics = new Statistics();
            statistics.setApplication("hello"+i%10);
            statistics.setConcurrent(2121);
            statistics.setElapsed(2121);
            statistics.setFailureCount(21);
            statistics.setHost("192.168.0.1");
            statistics.setInput(111);
            statistics.setKbps(2221);
            statistics.setMethod("method");
            statistics.setOutput(212121);
            statistics.setRemoteAddress("21.2.2.1");
            statistics.setType(Statistics.ApplicationType.CONSUMER);
            statistics.setTps(1221);
            statistics.setTimestamp(System.currentTimeMillis());
            statistics.setSuccessCount(212121);
            statistics.setServiceInterface("fdfd.fdfd.fdfd");
            statistics.setRemoteType(Statistics.ApplicationType.PROVIDER);
            mysqlStatisticsStorage.storeStatistics(statistics);
        }
        System.out.println("Finished");
        System.in.read();
    }

    @Test
    public void testQueryStatisticsForMethod() throws Exception {

    }

    @Test
    public void testQueryMethodMonitorOverview() throws Exception {

    }

    @Test
    public void testQueryApplications() throws Exception {

    }

    @Test
    public void testQueryApplicationInfo() throws Exception {

    }

    @Test
    public void testQueryApplicationOverview() throws Exception {

    }

    @Test
    public void testQueryServiceOverview() throws Exception {

    }

    @Test
    public void testQueryServiceByApp() throws Exception {

    }
}