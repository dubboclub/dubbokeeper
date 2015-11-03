package com.dubboclub.web.model;

import com.dubboclub.monitor.model.Statistics;
import com.dubboclub.monitor.model.Usage;

import java.util.Collection;

/**
 * Created by bieber on 2015/10/25.
 */
public class MethodStatistics {

    private Collection<Statistics> statisticsCollection;

    private Collection<Usage> usageCollection;

    public Collection<Statistics> getStatisticsCollection() {
        return statisticsCollection;
    }

    public void setStatisticsCollection(Collection<Statistics> statisticsCollection) {
        this.statisticsCollection = statisticsCollection;
    }

    public Collection<Usage> getUsageCollection() {
        return usageCollection;
    }

    public void setUsageCollection(Collection<Usage> usageCollection) {
        this.usageCollection = usageCollection;
    }
}
