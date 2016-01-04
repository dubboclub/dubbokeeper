package com.dubboclub.dk.storage.mysql;

import com.dubboclub.dk.storage.StatisticsStorage;
import com.dubboclub.dk.storage.model.*;
import com.dubboclub.dk.storage.mysql.mapper.ApplicationMapper;
import com.dubboclub.dk.storage.mysql.mapper.StatisticsMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @date: 2015/12/14.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mysql.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public class MysqlStatisticsStorage implements StatisticsStorage,InitializingBean {


    private ApplicationMapper applicationMapper;

    private StatisticsMapper statisticsMapper;

    private DataSource dataSource;

    private TransactionTemplate transactionTemplate;

    private static final ConcurrentHashMap<String,ApplicationStatisticsStorage> APPLICATION_STORAGES = new ConcurrentHashMap<String, ApplicationStatisticsStorage>();

    @Override
    public void storeStatistics(Statistics statistics) {
        if(!APPLICATION_STORAGES.containsKey(statistics.getApplication().toLowerCase())){
            ApplicationStatisticsStorage applicationStatisticsStorage  = new ApplicationStatisticsStorage(applicationMapper,statisticsMapper,dataSource,transactionTemplate,statistics.getApplication(),true);
            ApplicationStatisticsStorage old = APPLICATION_STORAGES.putIfAbsent(statistics.getApplication().toLowerCase(),applicationStatisticsStorage);
            if(old==null){
                applicationStatisticsStorage.start();
            }
        }
        APPLICATION_STORAGES.get(statistics.getApplication().toLowerCase()).addStatistics(statistics);
    }

    @Override
    public List<Statistics> queryStatisticsForMethod(String application, String serviceInterface, String method, long startTime, long endTime) {
        return statisticsMapper.queryStatisticsForMethod(application,startTime,endTime,serviceInterface,method);
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

    @Override
    public void afterPropertiesSet() throws Exception {
        Collection<String> apps = applicationMapper.listApps();
        for(String app:apps){
            ApplicationStatisticsStorage applicationStatisticsStorage =  new ApplicationStatisticsStorage(applicationMapper,statisticsMapper,dataSource,transactionTemplate,app);
            APPLICATION_STORAGES.put(app,applicationStatisticsStorage);
            applicationStatisticsStorage.start();
        }
    }

    public void setApplicationMapper(ApplicationMapper applicationMapper) {
        this.applicationMapper = applicationMapper;
    }

    public void setStatisticsMapper(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
