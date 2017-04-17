package com.dubboclub.dk.collector.support;


import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.dubboclub.dk.collector.Configuration;
import com.dubboclub.dk.collector.disruptor.SpanEvent;
import com.dubboclub.dk.collector.disruptor.SpanEventDisruptor;
import com.dubboclub.dk.storage.TraceDataStorage;
import com.dubboclub.dk.tracing.api.Span;
import com.dubboclub.dk.tracing.api.TracingCollector;
import com.lmax.disruptor.EventHandler;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by Zetas on 2016/7/11.
 */
public class DefaultCollectionService implements TracingCollector, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(DefaultCollectionService.class);

    private volatile ExecutorService executor;
    private volatile SpanEventDisruptor disruptor;
    private volatile TraceDataStorage traceDataStorage;

    public void setTraceDataStorage(TraceDataStorage traceDataStorage) {
        this.traceDataStorage = traceDataStorage;
    }

    public void afterPropertiesSet() throws Exception {
        start();
    }

    private class SpanEventHandle2Storage implements EventHandler<SpanEvent> {
        @Override
        public void onEvent(SpanEvent event, long sequence, boolean endOfBatch) throws Exception {
            List<Span> spanList = event.getSpanList();
            if (spanList != null) {
                executor.execute(new InsertTask(spanList, traceDataStorage));
            }
        }
    }

    private class InsertTask implements Runnable {
        private List<Span> insertingSpanList;
        private TraceDataStorage storage;

        private InsertTask(List<Span> spanList, TraceDataStorage traceDataStorage) {
            insertingSpanList = spanList;
            storage = traceDataStorage;
        }

        public void run() {
            logger.debug("add span, size: {}", insertingSpanList.size());
            storage.storeTraceData(insertingSpanList);
        }
    }

    public DefaultCollectionService(Configuration configuration) {
        int threadPoolSize = configuration.getThreadPoolSize() == null ? 16 : configuration.getThreadPoolSize();
        executor = new ThreadPoolExecutor(
                threadPoolSize,
                threadPoolSize,
                1,
                TimeUnit.DAYS,
                new SynchronousQueue<Runnable>(),
                new NamedThreadFactory("Dst-span-insert-task"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        disruptor = new SpanEventDisruptor(new SpanEventHandle2Storage());
    }

    private void start() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                cancel();
            }
        });
    }

    private void cancel() {
        executor.shutdown();
    }

    public void push(List<Span> spanList) {
        disruptor.produce(spanList);
    }

}
