package com.dubboclub.dk.tracing.api;

import java.util.List;

/**
 * Created by Zetas on 2016/7/7.
 */
public interface TracingCollector {

    void push(List<Span> spanList);
}
