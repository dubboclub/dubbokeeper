package com.dubboclub.dk.tracing.client.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Zetas on 2016/7/8.
 */
public class Sampler {

    private static final int BASE_TIMES = 100;

    private static class MetaData {
        private long beforeSampleTime;
        private AtomicLong counter = new AtomicLong(0);
    }

    private static ConcurrentMap<String, MetaData> samplerMap = new ConcurrentHashMap<String, MetaData>();

    public static boolean isSample(String serviceName) {
        MetaData metaData = getOrCreate(serviceName);
        long currentTimeMillis = System.currentTimeMillis();
        long n = metaData.counter.incrementAndGet();
        boolean isSample = true;

        if (currentTimeMillis - metaData.beforeSampleTime < 1000) {
            if (n > BASE_TIMES) {
                n = n % 10;
                if (n != 0) {
                    isSample = false;
                }
            }
        } else {
            metaData.counter.set(0);
            metaData.beforeSampleTime = currentTimeMillis;
        }

        return isSample;
    }

    private static MetaData getOrCreate(String serviceName) {
        MetaData metaData = samplerMap.get(serviceName);
        if (metaData == null) {
            metaData = new MetaData();
            metaData = samplerMap.putIfAbsent(serviceName, metaData);
        }
        return metaData == null ? samplerMap.get(serviceName) : metaData;
    }


}
