package com.dubboclub.monitor;

import java.util.List;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.monitor.MonitorService;

/**
 * Created by bieber on 2015/6/1.
 * 
 * @author bieber
 * @author shevawen
 * 
 */
public class DubboKeeperMonitorService implements MonitorService {

	private static final String POISON_PROTOCOL = "poison";
	

	public DubboKeeperMonitorService() {

	}

	@Override
	public void collect(URL statistics) {

	}

	@Override
	public List<URL> lookup(URL url) {
		return null;
	}
}
