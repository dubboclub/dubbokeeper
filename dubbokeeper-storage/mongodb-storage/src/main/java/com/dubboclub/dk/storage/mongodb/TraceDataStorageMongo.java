package com.dubboclub.dk.storage.mongodb;


import com.dubboclub.dk.storage.TraceDataHandler;
import com.dubboclub.dk.storage.TraceDataStorage;
import com.dubboclub.dk.tracing.api.Span;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Zetas on 2016/7/11.
 */
public class TraceDataStorageMongo implements TraceDataStorage{

    private static final Logger logger = LoggerFactory.getLogger(TraceDataStorageMongo.class);

    private List<TraceDataHandler> handlers;

    @Override
    public void storeTraceData(List<Span> spans) {
        logger.debug("received spans, size: {}", spans.size());
        for (Span span : spans) {
            logger.debug("{}", span);
            for (TraceDataHandler handler : handlers) {
                handler.handle(span);
            }
        }
    }

    public List<TraceDataHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<TraceDataHandler> handlers) {
        this.handlers = handlers;
    }
}
