package com.dubboclub.dk.web.model;


import com.dubboclub.dk.storage.model.Statistics;

import java.util.Collection;

/**
 * Created by bieber on 2015/10/25.
 */
public class MethodStatistics {

    private Collection<Statistics> statisticsCollection;


    public Collection<Statistics> getStatisticsCollection() {
        return statisticsCollection;
    }

    public void setStatisticsCollection(Collection<Statistics> statisticsCollection) {
        this.statisticsCollection = statisticsCollection;
    }


}
