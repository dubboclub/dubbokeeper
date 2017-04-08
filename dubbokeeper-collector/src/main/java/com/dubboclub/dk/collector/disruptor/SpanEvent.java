package com.dubboclub.dk.collector.disruptor;



import com.dubboclub.dk.tracing.api.Span;
import java.util.List;

/**
 * Created by zetas on 2016/7/16.
 */
public class SpanEvent {

    public List<Span> spanList;

    public List<Span> getSpanList() {
        return spanList;
    }

    public void setSpanList(List<Span> spanList) {
        this.spanList = spanList;
    }
}
