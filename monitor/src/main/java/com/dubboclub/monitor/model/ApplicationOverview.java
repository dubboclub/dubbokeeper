package com.dubboclub.monitor.model;

import java.util.List;

/**
 * Created by bieber on 2015/11/3.
 */
public class ApplicationOverview {

    private List<MethodMonitorOverview> methodMonitorOverviewList;

    public List<MethodMonitorOverview> getMethodMonitorOverviewList() {
        return methodMonitorOverviewList;
    }

    public void setMethodMonitorOverviewList(List<MethodMonitorOverview> methodMonitorOverviewList) {
        this.methodMonitorOverviewList = methodMonitorOverviewList;
    }
}
