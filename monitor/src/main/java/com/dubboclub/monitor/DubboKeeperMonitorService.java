package com.dubboclub.monitor;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.monitor.MonitorService;

import java.util.List;

/**
 * Created by bieber on 2015/6/1.
 */
public class DubboKeeperMonitorService implements MonitorService {

    @Override
    public void collect(URL url) {

    }

    @Override
    public List<URL> lookup(URL url) {
        return null;
    }
}
