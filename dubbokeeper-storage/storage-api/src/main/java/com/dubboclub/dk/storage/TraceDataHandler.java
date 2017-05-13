package com.dubboclub.dk.storage;


import com.dubboclub.dk.tracing.api.Span;

/**
 * Created by Zetas on 2016/7/11.
 */
public interface TraceDataHandler {

    void handle(Span span);
}
