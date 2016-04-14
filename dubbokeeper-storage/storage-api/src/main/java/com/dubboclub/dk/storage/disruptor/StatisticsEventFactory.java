package com.dubboclub.dk.storage.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * StatisticsEventFactory
 * Created by bieber.bibo on 16/4/14
 */

public class StatisticsEventFactory implements EventFactory<StatisticsEvent> {
    @Override
    public StatisticsEvent newInstance() {
        return new StatisticsEvent();
    }
}
