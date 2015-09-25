package com.dubboclub.monitor;

import java.util.List;
import java.util.concurrent.*;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.monitor.MonitorService;
import com.dubboclub.monitor.model.Statistics;
import com.dubboclub.monitor.storage.StatisticsStorage;

import javax.management.monitor.Monitor;

/**
 * Created by bieber on 2015/6/1.
 * 
 * @author bieber
 * @author shevawen
 * 
 */
public class DubboKeeperMonitorService implements MonitorService {
	
	public static final String HOST_KEY="host";
	
	public static final String REMOTE_ADDRESS="remoteAddress";
	
	public static final String INVOKE_STAT="invokeStat";
	
	public static final String REMOTE_TYPE="remoteType";
	
	public static final String APPLICATION_TYPE="applicationType";

	private static ExecutorService WRITE_INTO_LUCENE_EXECUTOR;
	
	private StatisticsStorage statisticsStorage;
	
	public DubboKeeperMonitorService() {
		WRITE_INTO_LUCENE_EXECUTOR = Executors.newFixedThreadPool(Integer.parseInt(ConfigUtils.getProperty("monitor.writer.size", Runtime.getRuntime().availableProcessors()+"")));
	}

	@Override
	public void collect(URL statisticsURL) {
		Statistics statistics = new Statistics();
		statistics.setTimestamp(System.currentTimeMillis());
		statistics.setApplication(statisticsURL.getParameter(MonitorService.APPLICATION));
		statistics.setConcurrent(statisticsURL.getParameter(MonitorService.CONCURRENT, 0));
		statistics.setElapsed(statisticsURL.getParameter(MonitorService.ELAPSED, 0));
		statistics.setHost(statisticsURL.getHost());
		statistics.setServiceInterface(statisticsURL.getParameter(MonitorService.INTERFACE));
		statistics.setMethod(statisticsURL.getParameter(MonitorService.METHOD));
		statistics.setInput(statisticsURL.getParameter(MonitorService.INPUT,0));
		statistics.setOutput(statisticsURL.getParameter(MonitorService.OUTPUT,0));
		if(statisticsURL.hasParameter(MonitorService.FAILURE)){
			statistics.setInvokeStat(false);
		}else{
			statistics.setInvokeStat(true);
		}
		if(statisticsURL.hasParameter(MonitorService.PROVIDER)){
			statistics.setType(Statistics.ApplicationType.CONSUMER);
			statistics.setRemoteType(Statistics.ApplicationType.PROVIDER);
			statistics.setRemoteAddress(statisticsURL.getParameter(MonitorService.PROVIDER));
		}else{
			statistics.setType(Statistics.ApplicationType.PROVIDER);
			statistics.setRemoteType(Statistics.ApplicationType.CONSUMER);
			statistics.setRemoteAddress(statisticsURL.getParameter(MonitorService.CONSUMER));
		}
		WRITE_INTO_LUCENE_EXECUTOR.submit(new StatisticsRunner(statistics));
	}

	
	
	
	@Override
	public List<URL> lookup(URL url) {
		return null;
	}
	
	class StatisticsRunner implements Runnable{
		private Statistics statistics;
		StatisticsRunner(Statistics statistics){
			this.statistics = statistics;
		}
		@Override
		public void run() {
			statisticsStorage.storeStatistics(statistics);
		}
	}

	public void setStatisticsStorage(StatisticsStorage statisticsStorage) {
		this.statisticsStorage = statisticsStorage;
	}
}
