package com.dubboclub.dk.storage.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bieber on 2015/11/3.
 */
public class StatisticsOverview implements Serializable {

    private List<ConcurrentItem> concurrentItems;

    private List<ElapsedItem> elapsedItems;

    private List<FaultItem> faultItems;

    private List<SuccessItem> successItems;

    public List<ConcurrentItem> getConcurrentItems() {
        return concurrentItems;
    }

    public void setConcurrentItems(List<ConcurrentItem> concurrentItems) {
        this.concurrentItems = concurrentItems;
    }

    public List<ElapsedItem> getElapsedItems() {
        return elapsedItems;
    }

    public void setElapsedItems(List<ElapsedItem> elapsedItems) {
        this.elapsedItems = elapsedItems;
    }

    public List<FaultItem> getFaultItems() {
        return faultItems;
    }

    public void setFaultItems(List<FaultItem> faultItems) {
        this.faultItems = faultItems;
    }

    public List<SuccessItem> getSuccessItems() {
        return successItems;
    }

    public void setSuccessItems(List<SuccessItem> successItems) {
        this.successItems = successItems;
    }
}
