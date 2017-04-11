package com.dubboclub.dk.storage.mongodb.handle;


import com.dubboclub.dk.storage.mongodb.TraceDataHandle;
import com.dubboclub.dk.storage.mongodb.dao.TracingApplicationDao;
import com.dubboclub.dk.storage.mongodb.dto.TracingApplicationDto;
import com.dubboclub.dk.tracing.api.Annotation;
import com.dubboclub.dk.tracing.api.BinaryAnnotation;
import com.dubboclub.dk.tracing.api.Endpoint;
import com.dubboclub.dk.tracing.api.Span;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by Zetas on 2016/7/11.
 */
public class ApplicationHandle implements TraceDataHandle, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(ApplicationHandle.class);

    private static final ConcurrentMap<Integer, Boolean> applicationNameHashMap = new ConcurrentHashMap<Integer, Boolean>();

    private TracingApplicationDao dao;
    private SyncLoadTask syncLoadApplicationThread;
    private BlockingQueue<String> queue;

    private class SyncLoadTask extends Thread {
        private SyncLoadTask() {
            setName("Dst-application-sync-load-task-thread");
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    String applicationName = queue.take();
                    addApplication(applicationName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ApplicationHandle() {
        syncLoadApplicationThread = new SyncLoadTask();
        queue = new LinkedBlockingQueue<String>();
    }

    public void setDao(TracingApplicationDao dao) {
        this.dao = dao;
    }

    @Override
    public void handle(List<Span> spanList) {
        logger.debug("span list size: {}", spanList.size());
        for (Span span : spanList) {
            handle(span);
        }
    }

    private void handle(Span span) {
        for (Annotation annotation : span.getAnnotationList()) {
            handle(annotation);
        }
        for (BinaryAnnotation binaryAnnotation : span.getBinaryAnnotationList()) {
            handle(binaryAnnotation);
        }
    }

    private void handle(Annotation annotation) {
        handle(annotation.getHost());
    }

    private void handle(BinaryAnnotation annotation) {
        handle(annotation.getHost());
    }

    private void handle(Endpoint endpoint) {
        if (!applicationNameHashMap.containsKey(endpoint.getApplicationName().hashCode())) {
            prepareAddApplication(endpoint.getApplicationName());
        }
    }

    private void prepareAddApplication(String applicationName) {
        Boolean value = applicationNameHashMap.putIfAbsent(applicationName.hashCode(), false);
        if (value == null) {
            queue.add(applicationName);
        }
    }

    private void addApplication(String applicationName) {
        TracingApplicationDto dto = new TracingApplicationDto();
        dto.setApplicationId(applicationName.hashCode());
        dto.setApplicationName(applicationName);
        dto.setCreateTime(System.currentTimeMillis());
        dao.add(dto);

        applicationNameHashMap.put(applicationName.hashCode(), true);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadApplication();
        start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cancel();
            }
        });
    }

    private void loadApplication() {
        List<TracingApplicationDto> dtoList = dao.findAll();
        for (TracingApplicationDto dto : dtoList) {
            applicationNameHashMap.put(dto.getApplicationId(), true);
        }
    }

    private void start() {
        syncLoadApplicationThread.start();
    }

    private void cancel() {
        syncLoadApplicationThread.interrupt();
    }

}
