package com.dubboclub.dk.storage.mongodb.handler;


import com.dubboclub.dk.storage.TraceDataHandler;
import com.dubboclub.dk.storage.model.Service;
import com.dubboclub.dk.storage.mongodb.dao.TracingServiceDao;
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
public class ServiceHandler implements TraceDataHandler, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(ServiceHandler.class);

    private static final ConcurrentMap<Integer, Boolean> serviceNameHashMap = new ConcurrentHashMap<Integer, Boolean>();

    private TracingServiceDao dao;
    private SyncLoadTask syncLoadServiceThread;
    private BlockingQueue<Span> queue;

    private class SyncLoadTask extends Thread {
        private SyncLoadTask() {
            setName("Dst-service-sync-load-task-thread");
        }

        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    Span span = queue.take();
                    addService(span);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ServiceHandler() {
        syncLoadServiceThread = new SyncLoadTask();
        queue = new LinkedBlockingQueue<Span>();
    }

    public void setDao(TracingServiceDao dao) {
        this.dao = dao;
    }

    @Override
    public void handle(Span span) {
        if (!serviceNameHashMap.containsKey(span.getServiceName().hashCode())) {
            prepareAddService(span);
        }
    }

    private void prepareAddService(Span span) {
        Boolean value = serviceNameHashMap.putIfAbsent(span.getServiceName().hashCode(), false);
        if (value == null) {
            queue.add(span);
        }
    }

    private void addService(Span span) {
        Service service = new Service();
        service.setId(service.getName().hashCode());
        service.setName(span.getServiceName());
        service.setTimestamp(System.currentTimeMillis());
        dao.add(service);

        serviceNameHashMap.put(span.getServiceName().hashCode(), true);
    }

    public void afterPropertiesSet() throws Exception {
        loadService();
        start();
    }

    private void loadService() {
        List<Service> services = dao.findAll();
        if (services != null) {
            for (Service service : services) {
                serviceNameHashMap.put(service.getId(), true);
            }
        }
    }

    private void start() {
        syncLoadServiceThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cancel();
            }
        });
    }

    private void cancel() {
        syncLoadServiceThread.interrupt();
    }
}
