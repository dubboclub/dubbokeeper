package com.dubboclub.monitor.storage;

import com.dubboclub.monitor.model.MethodMonitorOverview;
import com.dubboclub.monitor.model.Statistics;
import com.dubboclub.monitor.model.Usage;

import java.util.Collection;
import java.util.List;

/**
 * Created by bieber on 2015/9/25.
 */
public interface StatisticsStorage {
    
    public void storeStatistics(Statistics statistics);


    /**
     * 查看某个应用的某个接口在制定区间内的监控情况
     * @param application
     * @param serviceInterface
     * @param startTime
     * @param endTime
     * @return
     */
    public List<Statistics> queryStatisticsForMethod(String application, String serviceInterface,String method, long startTime, long endTime);

    /**
     * 查看某个服务的监控概要
     * @param application
     * @param serviceInterface
     * @return
     */
    public Collection<MethodMonitorOverview> queryMethodMonitorOverview(String application,String serviceInterface,int methodSize,long startTime,long endTime);

    /**
     * 查看某个方法使用情况
     * @param application
     * @param service
     * @param method
     * @param startTime
     * @param endTime
     * @return
     */
    public Collection<Usage> queryMethodUsage(String application,String service,String method,long startTime,long endTime);

    /**
     * 查看某个服务使用情况
     * @param application
     * @param service
     * @param method
     * @param startTime
     * @param endTime
     * @return
     */
    public Collection<Usage> queryServiceUsage(String application,String service,long startTime,long endTime);
    
    
    
}
