package com.dubboclub.dk.storage.mongodb.handler;


import com.dubboclub.dk.storage.TraceDataHandler;
import com.dubboclub.dk.storage.model.Application;
import com.dubboclub.dk.storage.mongodb.dao.TracingApplicationDao;
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
public class ApplicationHandler implements TraceDataHandler, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(ApplicationHandler.class);

    private static final ConcurrentMap<Integer, Boolean> APPLICATIONS_CACHE = new ConcurrentHashMap<Integer, Boolean>();

    private TracingApplicationDao dao;
    private SyncLoadTask syncLoadTask;
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

    public ApplicationHandler() {
        syncLoadTask = new SyncLoadTask();
        queue = new LinkedBlockingQueue<String>();
    }

    public void setDao(TracingApplicationDao dao) {
        this.dao = dao;
    }

    @Override
    public void handle(Span span) {
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
        if (!APPLICATIONS_CACHE.containsKey(endpoint.getApplicationName().hashCode())) {
            prepareAddApplication(endpoint.getApplicationName());
        }
    }

    private void prepareAddApplication(String applicationName) {
        Boolean value = APPLICATIONS_CACHE.putIfAbsent(applicationName.hashCode(), false);
        if (value == null) {
            queue.add(applicationName);
        }
    }

    private void addApplication(String applicationName) {
        Application application = new Application();
        application.setId(applicationName.hashCode());
        application.setName(applicationName);
        application.setTimestamp(System.currentTimeMillis());
        dao.add(application);

        APPLICATIONS_CACHE.put(applicationName.hashCode(), true);
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
        List<Application> applications = dao.findAll();
        for (Application application : applications) {
            APPLICATIONS_CACHE.put(application.getId(), true);
            logger.debug("preload applications: {}:{}", application.getId(), APPLICATIONS_CACHE.get(application.getId()));
        }
    }

    private void start() {
        syncLoadTask.start();
    }

    private void cancel() {
        syncLoadTask.interrupt();
    }

}
