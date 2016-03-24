package com.dubboclub.dk.storage.mongodb;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.dubboclub.dk.storage.model.ApplicationInfo;
import com.dubboclub.dk.storage.model.Statistics;
import com.dubboclub.dk.storage.mongodb.dao.ApplicationDao;
import com.dubboclub.dk.storage.mongodb.dao.StatisticsDao;
import org.apache.commons.lang.time.DateUtils;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by hidehai on 2016/3/23.
 */
public class ApplicationStatisticsStorage extends Thread{

    Logger LOGGER = LoggerFactory.getLogger("dubbokeeper-server");

    private ApplicationDao applicationDao;

    private StatisticsDao statisticsDao;

    private String application;

    private volatile long maxElapsed;

    private volatile long maxConcurrent;

    private volatile int maxFault;

    private volatile int maxSuccess;

    private int type;

    private volatile boolean isWriting=false;

    private ConcurrentLinkedQueue<Statistics> statisticsCollection = new ConcurrentLinkedQueue<Statistics>();

    private static final int WRITE_INTERVAL= Integer.parseInt(ConfigUtils.getProperty("mongodb.commit.interval", "100"));


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
        this.applicationDao = applicationDao;
        this.statisticsDao = statisticsDao;
        this.application = applicationName;
        this.setName(application+"_statisticsWriter");
        this.setDaemon(true);
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
    public void run() {
        LOGGER.info(String.format("%s is running.",getName()));
      while(true){
          isWriting = true;
          List<Statistics> statisticseList = new ArrayList<Statistics>(statisticsCollection);
          if(statisticseList.size() > 0){
              if(batchInsert(statisticseList)){
                  statisticsCollection.clear();
              }
          }
          isWriting = false;
          try {
              Thread.sleep(WRITE_INTERVAL);
          } catch (InterruptedException e) {
              LOGGER.error(e.getMessage(),e);
          }
      }
    }

    /**
     *
     * @param statistics
     */
    public void addStatistics(Statistics statistics){
        while(isWriting){
            //waiting write finished
        }
        if(WRITE_INTERVAL<=0){
            statisticsDao.addOne(application,statistics);
        }else{
            statisticsCollection.add(statistics);
        }
        if(maxFault<statistics.getFailureCount()){
            maxFault=statistics.getFailureCount();
        }
        if(maxSuccess<statistics.getSuccessCount()){
            maxSuccess=statistics.getSuccessCount();
        }
        if(maxConcurrent<statistics.getConcurrent()){
            maxConcurrent = statistics.getConcurrent();
        }
        if(maxElapsed<statistics.getElapsed()){
            maxElapsed = statistics.getElapsed();
        }
        if((type==0&&statistics.getType()== Statistics.ApplicationType.PROVIDER)||(type==1&&statistics.getType()== Statistics.ApplicationType.CONSUMER)){
            synchronized (this){
                if(type!=2){
                    applicationDao.updateAppType(application,2);
                    type=2;
                }
            }
        }
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


    public String getApplication() {
        return application;
    }

    public long getMaxElapsed() {
        return maxElapsed;
    }

    public long getMaxConcurrent() {
        return maxConcurrent;
    }

    public int getMaxFault() {
        return maxFault;
    }

    public int getMaxSuccess() {
        return maxSuccess;
    }

    public int getType() {
        return type;
    }
}
