package com.dubboclub.dk.tracing.client;

import com.alibaba.dubbo.common.extension.SPI;
import com.dubboclub.dk.tracing.api.TracingCollector;


/**
 * TracingCollectorFactory
 * Created by bieber.bibo on 16/7/18
 */
@SPI(DstConstants.DEFAULT_COLLECTOR_TYPE)
public interface TracingCollectorFactory {

    /**
     * 监控链路的数据同步器
     * @return
     */
    public TracingCollector getTracingCollector();

}
