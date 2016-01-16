package com.dubboclub.dk.storage.mysql;

import com.dubboclub.dk.storage.StatisticsStorage;
import com.dubboclub.dk.storage.model.*;
import com.dubboclub.dk.storage.mysql.mapper.ApplicationMapper;
import com.dubboclub.dk.storage.mysql.mapper.StatisticsMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
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
            ApplicationStatisticsStorage applicationStatisticsStorage  = new ApplicationStatisticsStorage(applicationMapper,statisticsMapper,dataSource,transactionTemplate,statistics.getApplication(),statistics.getType()== Statistics.ApplicationType.CONSUMER?0:1,true);
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
        List<String> methods = statisticsMapper.queryMethodForService(application,serviceInterface);
        Collection<MethodMonitorOverview> methodMonitorOverviews = new ArrayList<MethodMonitorOverview>(methods.size());
        for(String method:methods){
            MethodMonitorOverview methodMonitorOverview = new MethodMonitorOverview();
            methodMonitorOverview.setMaxConcurrent(statisticsMapper.<Long>queryMethodMaxItemByService("concurrent",application,serviceInterface,startTime,endTime));
            methodMonitorOverview.setMaxElapsed(statisticsMapper.<Long>queryMethodMaxItemByService("elapsed",application,serviceInterface,startTime,endTime));
            methodMonitorOverview.setMaxFailure(statisticsMapper.<Integer>queryMethodMaxItemByService("failureCount",application,serviceInterface,startTime,endTime));
            methodMonitorOverview.setMaxInput(statisticsMapper.<Long>queryMethodMaxItemByService("input",application,serviceInterface,startTime,endTime));
            methodMonitorOverview.setMaxKbps(statisticsMapper.<Double>queryMethodMaxItemByService("kbps",application,serviceInterface,startTime,endTime));
            methodMonitorOverview.setMaxOutput(statisticsMapper.<Long>queryMethodMaxItemByService("output",application,serviceInterface,startTime,endTime));
            methodMonitorOverview.setMaxSuccess(statisticsMapper.<Integer>queryMethodMaxItemByService("successCount",application,serviceInterface,startTime,endTime));
            methodMonitorOverview.setMaxTps(statisticsMapper.<Double>queryMethodMaxItemByService("tps",application,serviceInterface,startTime,endTime));
            methodMonitorOverviews.add(methodMonitorOverview);
        }
        return methodMonitorOverviews;
    }

    @Override
    public Collection<ApplicationInfo> queryApplications() {
        Collection<ApplicationInfo> applicationInfos =  applicationMapper.listApps();
        for(ApplicationInfo applicationInfo:applicationInfos){
            ApplicationStatisticsStorage applicationStatisticsStorage =APPLICATION_STORAGES.get(applicationInfo.getApplicationName());
            applicationInfo.setMaxConcurrent(applicationStatisticsStorage.getMaxConcurrent());
            applicationInfo.setMaxElapsed(applicationStatisticsStorage.getMaxElapsed());
            applicationInfo.setMaxFault(applicationStatisticsStorage.getMaxFault());
            applicationInfo.setMaxSuccess(applicationStatisticsStorage.getMaxSuccess());
        }
        return applicationInfos;
    }

    @Override
    public ApplicationInfo queryApplicationInfo(String application, long start, long end) {
        ApplicationStatisticsStorage applicationStatisticsStorage = APPLICATION_STORAGES.get(application);
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setApplicationName(applicationStatisticsStorage.getApplication());
        applicationInfo.setApplicationType(applicationStatisticsStorage.getType());
        applicationInfo.setMaxConcurrent(statisticsMapper.queryMaxConcurrent(application, start, end));
        applicationInfo.setMaxElapsed(statisticsMapper.queryMaxElapsed(application, start, end));
        applicationInfo.setMaxFault(statisticsMapper.queryMaxFault(application, start, end));
        applicationInfo.setMaxSuccess(statisticsMapper.queryMaxSuccess(application, start, end));
        return applicationInfo;
    }

    @Override
    public StatisticsOverview queryApplicationOverview(String application, long start, long end) {
        StatisticsOverview statisticsOverview = new StatisticsOverview();
        List<Statistics>  statisticses = statisticsMapper.queryApplicationOverview(application,"concurrent",start,end);
        List<ConcurrentItem> concurrentItems = new ArrayList<ConcurrentItem>();
        statisticsOverview.setConcurrentItems(concurrentItems);
        for(Statistics statistics:statisticses){
            ConcurrentItem concurrentItem = new ConcurrentItem();
            convertItem(concurrentItem,statistics);
            concurrentItem.setConcurrent(statistics.getConcurrent());
            concurrentItems.add(concurrentItem);
        }
        statisticses = statisticsMapper.queryApplicationOverview(application,"ellapsed",start,end);
        List<ElapsedItem> elapsedItems = new ArrayList<ElapsedItem>();
        statisticsOverview.setElapsedItems(elapsedItems);
        for(Statistics statistics:statisticses){
            ElapsedItem elapsedItem = new ElapsedItem();
            convertItem(elapsedItem,statistics);
            elapsedItem.setElapsed(statistics.getElapsed());
            elapsedItems.add(elapsedItem);
        }
        statisticses = statisticsMapper.queryApplicationOverview(application,"failureCount",start,end);
        List<FaultItem> faultItems = new ArrayList<FaultItem>();
        statisticsOverview.setFaultItems(faultItems);
        for(Statistics statistics:statisticses){
            FaultItem faultItem = new FaultItem();
            convertItem(faultItem,statistics);
            faultItem.setFault(statistics.getFailureCount());
            faultItems.add(faultItem);
        }
        statisticses = statisticsMapper.queryApplicationOverview(application,"successCount",start,end);
        List<SuccessItem> successItems = new ArrayList<SuccessItem>();
        statisticsOverview.setSuccessItems(successItems);
        for(Statistics statistics:statisticses){
            SuccessItem successItem = new SuccessItem();
            convertItem(successItem,statistics);
            successItem.setSuccess(statistics.getSuccessCount());
            successItems.add(successItem);
        }
        return statisticsOverview;
    }

    private void convertItem(BaseItem item,Statistics statistics){
        item.setMethod( statistics.getMethod());
        item.setService(statistics.getServiceInterface());
        item.setTimestamp(statistics.getTimestamp());
        item.setRemoteType(statistics.getRemoteType().toString());
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
        Collection<ApplicationInfo> apps = applicationMapper.listApps();
        for(ApplicationInfo app:apps){
            ApplicationStatisticsStorage applicationStatisticsStorage =  new ApplicationStatisticsStorage(applicationMapper,statisticsMapper,dataSource,transactionTemplate,app.getApplicationName(),app.getApplicationType());
            APPLICATION_STORAGES.put(app.getApplicationName(),applicationStatisticsStorage);
            applicationStatisticsStorage.start();
            LOGGER.info("start application [{}] storage",app.getApplicationName());
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
