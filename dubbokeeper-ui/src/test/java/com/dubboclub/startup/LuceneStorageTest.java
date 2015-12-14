package com.dubboclub.startup;

import com.alibaba.fastjson.JSON;
import com.dubboclub.dk.storage.lucene.LuceneStatisticsStorage;
import com.dubboclub.dk.storage.model.MethodMonitorOverview;
import com.dubboclub.dk.storage.model.Statistics;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by bieber on 2015/10/7.
 */
public class LuceneStorageTest {

    public static  void main(String[] args) throws IOException {
        LuceneStatisticsStorage statisticsStorage = new LuceneStatisticsStorage();
        Properties properties = new Properties();
        properties.load(LuceneStorageTest.class.getClassLoader().getResourceAsStream("dubbo.properties"));
        com.alibaba.dubbo.common.utils.ConfigUtils.setProperties(properties);
        long start = System.currentTimeMillis();
        List<Statistics> statisticsList = statisticsStorage.queryStatisticsForMethod("dubbo-consumer-demo", "com.bieber.dubbo.service.MyFirstDubboService", "sayHello", System.currentTimeMillis() - 60 * 1000*1, System.currentTimeMillis());
        System.out.println(JSON.toJSONString(statisticsList));
        System.out.println("size:"+statisticsList.size());
        System.out.println("elapsed:" + (System.currentTimeMillis() - start));
        Collection<MethodMonitorOverview> methodMonitorOverviews = statisticsStorage.queryMethodMonitorOverview("dubbo-consumer-demo", "com.bieber.dubbo.service.MyFirstDubboService", 1, System.currentTimeMillis() - 60 * 1000*10, System.currentTimeMillis());
        System.out.println(JSON.toJSONString(methodMonitorOverviews));
    }
}
