package com.dubboclub.dk.storage;

import com.dubboclub.dk.tracing.api.Span;
import java.util.List;

/**
 * <p>Created by qct on 2017/4/12.
 */
public interface TraceDataStorage {

    /**
     * 持久化trace数据
     * @param spans trace 数据
     */
    void storeTraceData(List<Span> spans);
}
