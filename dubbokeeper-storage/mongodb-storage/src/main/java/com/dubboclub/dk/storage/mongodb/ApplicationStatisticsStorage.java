package com.dubboclub.dk.storage.mongodb;

import com.dubboclub.dk.storage.AbstractApplicationStatisticsStorage;
import com.dubboclub.dk.storage.model.ApplicationInfo;
import com.dubboclub.dk.storage.model.Statistics;
import com.dubboclub.dk.storage.mongodb.dao.ApplicationDao;
import com.dubboclub.dk.storage.mongodb.dao.StatisticsDao;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.time.DateUtils;

/**
 * Created by hidehai on 2016/3/23.
 */
public class ApplicationStatisticsStorage extends AbstractApplicationStatisticsStorage{

    private ApplicationDao applicationDao;

    private StatisticsDao statisticsDao;

    private int type;

    public ApplicationStatisticsStorage(ApplicationDao applicationDao,
                                        StatisticsDao statisticsDao,
                                        String application,
                                        int type){
        this(applicationDao,statisticsDao,application,type,false);
    }

    public ApplicationStatisticsStorage(ApplicationDao applicationDao,
                                        StatisticsDao statisticsDao,
                                        String applicationName,
                                        int type,
                                        boolean needCreateTable){
        super(applicationName);
        this.applicationDao = applicationDao;
        this.statisticsDao = statisticsDao;
        this.application = applicationName;
        if(needCreateTable){
            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.setApplicationName(application);
            applicationInfo.setApplicationType(type);
            this.applicationDao.addApplication(applicationInfo);
        }
        init();
        this.type = type;
    }

    /**
     *
     */
    public void init(){
        long end = System.currentTimeMillis();
        long start = DateUtils.addDays(new Date(),-1).getTime();
        Statistics concurrent =statisticsDao.queryMaxItemByService(application,null,"concurrent",start,end);
        Statistics elapsed = statisticsDao.queryMaxItemByService(application,null,"elapsed",start,end);
        Statistics fault = statisticsDao.queryMaxItemByService(application,null,"failureCount",start,end);
        Statistics success = statisticsDao.queryMaxItemByService(application,null,"successCount",start,end);
        maxConcurrent =concurrent==null?0:concurrent.getConcurrent();
        maxElapsed = elapsed==null?0:elapsed.getElapsed();
        maxFault=fault==null?0:fault.getFailureCount();
        maxSuccess=success==null?0:success.getSuccessCount();
    }



    @Override
    protected void batchAddStatistics(List<Statistics> statisticsList) {
        batchInsert(statisticsList);
    }

    /**
     *
     * @param statisticsList
     * @return
     */
    public boolean batchInsert(final List<Statistics> statisticsList){
        statisticsDao.batchInsert(application,statisticsList);
        return true;
    }



    public int getType() {
        return type;
    }
}
