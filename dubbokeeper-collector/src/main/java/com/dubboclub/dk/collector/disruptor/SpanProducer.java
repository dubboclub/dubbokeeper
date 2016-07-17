package com.dubboclub.dk.collector.disruptor;

import com.lmax.disruptor.RingBuffer;
import net.dubboclub.tracing.api.Span;

import java.util.List;

/**
 * Created by zetas on 2016/7/16.
 */
public class SpanProducer {

    private RingBuffer<SpanEvent> traceDataEventRingBuffer;

    public SpanProducer(RingBuffer<SpanEvent> traceDataEventRingBuffer) {
        this.traceDataEventRingBuffer = traceDataEventRingBuffer;
    }

    public void produce(List<Span> spanList){
        long sequence = traceDataEventRingBuffer.next();
        try{
            SpanEvent spanEvent = traceDataEventRingBuffer.get(sequence);
            spanEvent.setSpanList(spanList);
        }finally {
            traceDataEventRingBuffer.publish(sequence);
        }
    }
}
