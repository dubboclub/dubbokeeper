package com.dubboclub.dk.tracing.client;

import com.dubboclub.dk.tracing.api.Span;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by Zetas on 2016/7/8.
 */
public class ContextHolder {

    //一个线程中span栈,用来存放一个线程中多个span情况
    private static ThreadLocal<Stack<Span>> localSpan = new ThreadLocal<Stack<Span>>(){
        @Override
        protected Stack<Span> initialValue() {
            return new Stack<Span>();
        }
    };

    private static ThreadLocal<Long> localTraceId = new ThreadLocal<Long>();

    private static ThreadLocal<Boolean> localSample = new ThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue() {
            return true;
        }
    };

    static Long getTraceId(){
       return localTraceId.get();
    }

    static void setTraceId(Long traceId){
        localTraceId.set(traceId);
    }

    static void removeTraceId(){
        localTraceId.remove();
    }

    static boolean isSample(){
        return localSample.get();
    }

    static void setLocalSample(boolean isSample){
        localSample.set(isSample);
    }

    static void removeSample(){
        localSample.remove();
    }

    static void setSpan(Span span) {
        ContextHolder.localSpan.get().push(span);
    }

    static Span getSpan() {
        try {
            return localSpan.get().peek();
        }catch (EmptyStackException e){
            return null;
        }
    }

    static Span popSpan(){
        try {
            return localSpan.get().pop();
        }catch (EmptyStackException e){
            return null;
        }
    }


    static void removeSpan() {
        localSpan.remove();
    }
    public static void removeAll() {
        if(localSpan.get().size()<=0){//span堆栈为空的时候才能清除
            removeSpan();
            removeTraceId();
            removeSample();
        }
    }


}
