package com.dubboclub.monitor.storage;

import com.dubboclub.monitor.model.Statistics;

import java.util.List;

/**
 * Created by bieber on 2015/9/25.
 */
public interface StatisticsStorage {
    
    public void storeStatistics(Statistics statistics);
 
    public List<Statistics> queryStatisticsByHost(String application,String host,long startTime,long endTime);
    
    public List<Statistics> queryStatisticsForInterface(String application,String serviceInterface,long startTime,long endTime);
    
    public List<Statistics> queryAllApplicationAbstractInfo();
    
    
    
    
}
