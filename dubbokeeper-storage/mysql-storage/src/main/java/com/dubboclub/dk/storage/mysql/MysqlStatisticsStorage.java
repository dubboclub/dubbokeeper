package com.dubboclub.dk.storage.mysql;

import com.dubboclub.dk.storage.StatisticsStorage;
import com.dubboclub.dk.storage.model.*;

import java.util.Collection;
import java.util.List;

/**
 * @date: 2015/12/14.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mysql.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public class MysqlStatisticsStorage implements StatisticsStorage {


    @Override
    public void storeStatistics(Statistics statistics) {

    }

    @Override
    public List<Statistics> queryStatisticsForMethod(String application, String serviceInterface, String method, long startTime, long endTime) {
        return null;
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
}
