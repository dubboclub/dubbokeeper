package com.dubboclub.dk.tracing.client.support.roketmq;


import com.dubboclub.dk.tracing.api.TracingCollector;
import com.dubboclub.dk.tracing.client.TracingCollectorFactory;

/**
 * RocketMqTracingCollectorFactory
 * Created by bieber.bibo on 16/8/11
 */

public class RocketMqTracingCollectorFactory implements TracingCollectorFactory {

    @Override
    public TracingCollector getTracingCollector() {
        return new RocketMqTracingCollector();
    }

}
