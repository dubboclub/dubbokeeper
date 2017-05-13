package com.dubboclub.dk.storage;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.dubboclub.dk.storage.disruptor.StatisticsEvent;
import com.dubboclub.dk.storage.disruptor.StatisticsEventFactory;
import com.dubboclub.dk.storage.disruptor.StatisticsProducer;
import com.dubboclub.dk.storage.model.Statistics;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * AbstractApplicationStatisticsStorage
 * Created by bieber.bibo on 16/4/14
 * Copyright@2016-16/4/14
 * 对应用监控数据保存的抽象,将disruptor融合到其中
 */

public abstract class AbstractApplicationStatisticsStorage implements EventHandler<StatisticsEvent> {

    private static final Logger logger = LoggerFactory.getLogger("Application-Writer");

    private  StatisticsEventFactory statisticsEventFactory;

    protected String application;

    private  StatisticsProducer statisticsProducer;

    protected volatile long maxElapsed;

    protected volatile long maxConcurrent;

    protected volatile int maxFault;

    protected volatile int maxSuccess;

    //默认是1分钟持久化一次
    private static final int WRITE_INTERVAL= Integer.parseInt(ConfigUtils.getProperty("monitor.write.interval","6000"));

    private long lastWrite=0;

    private List<Statistics> tempStatisticsContainer = new ArrayList<Statistics>();

    /**
     * 这个是单线程的,所以是线程安全的
     * @param event
     * @param sequence
     * @param endOfBatch
     * @throws Exception
     */
    @Override
    public void onEvent(StatisticsEvent event, long sequence, boolean endOfBatch) throws Exception {
        Statistics statistics = event.get();
        tempStatisticsContainer.add(statistics);
        if(statistics==null){
            return;
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
        if(System.currentTimeMillis()-lastWrite>WRITE_INTERVAL*1000){
            logger.info("starting writing statistics,last write "+lastWrite);
            batchAddStatistics(tempStatisticsContainer);
            logger.info("finished write statistics,write size "+tempStatisticsContainer.size());
            tempStatisticsContainer.clear();
            lastWrite=System.currentTimeMillis();
        }

    }


    public AbstractApplicationStatisticsStorage(String application) {
        this.application = application;

    }

    public void start(){
        statisticsEventFactory = new StatisticsEventFactory();
        Disruptor<StatisticsEvent> disruptor = new Disruptor<StatisticsEvent>(statisticsEventFactory,
                1024,
                new NamedThreadFactory(application+"-writer"),
                ProducerType.MULTI,
                new TimeoutBlockingWaitStrategy(5000, TimeUnit.MILLISECONDS));
        disruptor.handleEventsWith(this);
        disruptor.start();
        statisticsProducer = new StatisticsProducer(disruptor.getRingBuffer());
    }

    //暴露给外面的添加监控数据接口
    public final void addStatistics(Statistics statistics){
        statisticsProducer.produce(statistics);
    }



    protected abstract void batchAddStatistics(List<Statistics> statisticsList);


    public long getMaxConcurrent() {
        return maxConcurrent;
    }

    public long getMaxElapsed() {
        return maxElapsed;
    }

    public int getMaxFault() {
        return maxFault;
    }

    public int getMaxSuccess() {
        return maxSuccess;
    }

    public String getApplication() {
        return application;
    }
}
