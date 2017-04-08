package com.dubboclub.dk.collector.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * Created by zetas on 2016/7/16.
 */
public class SpanEventFactory implements EventFactory<SpanEvent> {
    @Override
    public SpanEvent newInstance() {
        return new SpanEvent();
    }
}
