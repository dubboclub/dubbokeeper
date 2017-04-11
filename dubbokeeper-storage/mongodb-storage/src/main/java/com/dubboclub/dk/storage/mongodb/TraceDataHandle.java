package com.dubboclub.dk.storage.mongodb;


import com.dubboclub.dk.tracing.api.Span;
import java.util.List;

/**
 * Created by Zetas on 2016/7/11.
 */
public interface TraceDataHandle {

    void handle(List<Span> spanList);
}
