package com.dubboclub.dk.collector.disruptor;

import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.dubboclub.dk.tracing.api.Span;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zetas on 2016/7/17.
 */
public class SpanEventDisruptor {

    private SpanProducer spanProducer;

    public SpanEventDisruptor(final EventHandler<SpanEvent>... handlers) {
        Disruptor<SpanEvent> disruptor = new Disruptor<SpanEvent>(
            new SpanEventFactory(),
            512 * 512,
            new NamedThreadFactory("Span-Event-Distributing"),
            ProducerType.MULTI,
            new TimeoutBlockingWaitStrategy(5000, TimeUnit.MILLISECONDS));
        disruptor.handleEventsWith(handlers);
        disruptor.start();
        disruptor.shutdown();

        spanProducer = new SpanProducer(disruptor.getRingBuffer());
    }

    public final void produce(List<Span> spanList) {
        spanProducer.produce(spanList);
    }

}
