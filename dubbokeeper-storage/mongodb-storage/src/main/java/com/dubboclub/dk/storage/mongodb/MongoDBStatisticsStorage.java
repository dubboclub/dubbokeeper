package com.dubboclub.dk.storage.mongodb;

import com.alibaba.dubbo.common.extension.ExtensionFactory;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.dubboclub.dk.storage.StatisticsStorage;
import com.dubboclub.dk.storage.model.*;
import com.dubboclub.dk.storage.mongodb.dao.ApplicationDao;
import com.dubboclub.dk.storage.mongodb.dao.StatisticsDao;
import com.dubboclub.dk.storage.mongodb.dto.TempMethodOveride;
import com.dubboclub.dk.storage.mongodb.dto.TempServiceOveride;
import com.mongodb.Function;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.data.Stat;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * @date: 2015/12/14.
 * @author: bieber ; hidehai;
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mongodb.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public class MongoDBStatisticsStorage implements StatisticsStorage {

    private static final ConcurrentHashMap<String,ApplicationStatisticsStorage> APPLICATION_STORAGES = new ConcurrentHashMap<String, ApplicationStatisticsStorage>();

    @Autowired
    private ApplicationDao applicationDao;
    @Autowired
    private StatisticsDao statisticsDao;


    @Override
    public void storeStatistics(Statistics statistics) {
        if(!APPLICATION_STORAGES.containsKey(statistics.getApplication().toLowerCase())){
            ApplicationStatisticsStorage applicationStatisticsStorage  = new ApplicationStatisticsStorage(applicationDao,statisticsDao,
                    statistics.getApplication(),
                    statistics.getType()== Statistics.ApplicationType.CONSUMER?0:1,true);
            ApplicationStatisticsStorage old = APPLICATION_STORAGES.putIfAbsent(statistics.getApplication().toLowerCase(),applicationStatisticsStorage);
            if(old==null){
                applicationStatisticsStorage.start();
            }
        }
        APPLICATION_STORAGES.get(statistics.getApplication().toLowerCase()).addStatistics(statistics);
    }

    @Override
    public List<Statistics> queryStatisticsForMethod(String application, String serviceInterface, String method, long startTime, long endTime) {
       return statisticsDao.queryStatisticsForMethod(application,serviceInterface,method,startTime,endTime);
    }

    @Override
    public Collection<MethodMonitorOverview> queryMethodMonitorOverview(String application, String serviceInterface, int methodSize, long startTime, long endTime) {
        List<TempMethodOveride> methods = statisticsDao.findMethodForService(application,serviceInterface);

        Collection<MethodMonitorOverview> methodMonitorOverviews = new ArrayList<MethodMonitorOverview>(methods.size());
        for(TempMethodOveride method : methods){
            MethodMonitorOverview methodMonitorOverview = new MethodMonitorOverview();
            Statistics concurrent= statisticsDao.findMethodMaxItemByService("concurrent",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics elapsed= statisticsDao.findMethodMaxItemByService("elapsed",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics failure= statisticsDao.findMethodMaxItemByService("failureCount",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics input= statisticsDao.findMethodMaxItemByService("input",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics kbps= statisticsDao.findMethodMaxItemByService("kbps",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics output= statisticsDao.findMethodMaxItemByService("output",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics success= statisticsDao.findMethodMaxItemByService("successCount",application,serviceInterface,method.getM(),startTime,endTime);
            Statistics tps= statisticsDao.findMethodMaxItemByService("tps",application,serviceInterface,method.getM(),startTime,endTime);

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
        List<ApplicationInfo>  applicationInfos = applicationDao.findAll();
        return applicationInfos;
    }

    @Override
    public ApplicationInfo queryApplicationInfo(String application, long start, long end) {
        //TODO fix
        return null;
    }

    @Override
    public StatisticsOverview queryApplicationOverview(String application, long start, long end) {
        StatisticsOverview statisticsOverview = new StatisticsOverview();
        List<Statistics> statisticses = statisticsDao.findApplicationOverview(application,"concurrent",start,end);
        fillConcurrentItem(statisticses,statisticsOverview);
        statisticses = statisticsDao.findApplicationOverview(application,"elapsed",start,end);
        fillElapsedItem(statisticses,statisticsOverview);
        statisticses = statisticsDao.findApplicationOverview(application,"failureCount",start,end);
        fillFaultItem(statisticses,statisticsOverview);
        statisticses = statisticsDao.findApplicationOverview(application,"successCount",start,end);
        fillSuccessItem(statisticses,statisticsOverview);
        return statisticsOverview;
    }

    @Override
    public StatisticsOverview queryServiceOverview(String application, String service, long start, long end) {
        StatisticsOverview statisticsOverview = new StatisticsOverview();
        List<Statistics> statisticses = statisticsDao.findServiceOverview(application,service,"concurrent",start,end);
        fillConcurrentItem(statisticses,statisticsOverview);
        statisticses = statisticsDao.findServiceOverview(application,service,"elapsed",start,end);
        fillElapsedItem(statisticses,statisticsOverview);
        statisticses = statisticsDao.findServiceOverview(application,service,"failureCount",start,end);
        fillFaultItem(statisticses,statisticsOverview);
        statisticses = statisticsDao.findServiceOverview(application,service,"successCount",start,end);
        fillSuccessItem(statisticses,statisticsOverview);
        return statisticsOverview;
    }

    @Override
    public Collection<ServiceInfo> queryServiceByApp(String application, long start, long end) {
        List<ServiceInfo> infos = statisticsDao.findServiceByApp(application);
        Statistics statistics = null;
        for(ServiceInfo info : infos){
            statistics = statisticsDao.queryMaxItemByService(application,info.getName(),"concurrent",start,end);
            info.setMaxConcurrent(statistics == null ? 0 : statistics.getConcurrent());
            statistics = statisticsDao.queryMaxItemByService(application,info.getName(),"elapsed",start,end);
            info.setMaxElapsed(statistics == null ? 0 : statistics.getElapsed());
            statistics = statisticsDao.queryMaxItemByService(application,info.getName(),"failureCount",start,end);
            info.setMaxFault(statistics == null ? 0 : statistics.getFailureCount());
            statistics = statisticsDao.queryMaxItemByService(application,info.getName(),"successCount",start,end);
            info.setMaxSuccess(statistics == null ? 0 : statistics.getSuccessCount());
        }
        return infos;
    }


    private void fillConcurrentItem(List<Statistics> statisticses,StatisticsOverview statisticsOverview){
        List<ConcurrentItem> concurrentItems = new ArrayList<ConcurrentItem>();
        statisticsOverview.setConcurrentItems(concurrentItems);
        for(Statistics statistics:statisticses){
            ConcurrentItem concurrentItem = new ConcurrentItem();
            convertItem(concurrentItem,statistics);
            concurrentItem.setConcurrent(statistics.getConcurrent());
            concurrentItems.add(concurrentItem);
        }
    }

    private void fillElapsedItem(List<Statistics> statisticses,StatisticsOverview statisticsOverview){
        List<ElapsedItem> elapsedItems = new ArrayList<ElapsedItem>();
        statisticsOverview.setElapsedItems(elapsedItems);
        for(Statistics statistics:statisticses){
            ElapsedItem elapsedItem = new ElapsedItem();
            convertItem(elapsedItem,statistics);
            elapsedItem.setElapsed(statistics.getElapsed());
            elapsedItems.add(elapsedItem);
        }
    }


    private void fillFaultItem(List<Statistics> statisticses,StatisticsOverview statisticsOverview){
        List<FaultItem> faultItems = new ArrayList<FaultItem>();
        statisticsOverview.setFaultItems(faultItems);
        for(Statistics statistics:statisticses){
            FaultItem faultItem = new FaultItem();
            convertItem(faultItem,statistics);
            faultItem.setFault(statistics.getFailureCount());
            faultItems.add(faultItem);
        }
    }

    private void fillSuccessItem(List<Statistics> statisticses,StatisticsOverview statisticsOverview){
        List<SuccessItem> successItems = new ArrayList<SuccessItem>();
        statisticsOverview.setSuccessItems(successItems);
        for(Statistics statistics:statisticses){
            SuccessItem successItem = new SuccessItem();
            convertItem(successItem,statistics);
            successItem.setSuccess(statistics.getSuccessCount());
            successItems.add(successItem);
        }
    }

    private void convertItem(BaseItem item,Statistics statistics){
        item.setMethod( statistics.getMethod());
        item.setService(statistics.getServiceInterface());
        item.setTimestamp(statistics.getTimestamp());
        item.setRemoteType(statistics.getRemoteType().toString());
    }

}
