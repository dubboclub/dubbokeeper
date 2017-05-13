package com.dubboclub.dk.monitor;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.monitor.MonitorService;
import com.dubboclub.dk.storage.StatisticsStorage;
import com.dubboclub.dk.storage.model.Statistics;
import java.math.BigDecimal;
import java.util.List;

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
	
	public static final String REMOTE_TYPE="remoteType";
	
	public static final String APPLICATION_TYPE="applicationType";

    public static final String TPS="tps";

    public static final String KBPS="kbps";

	private StatisticsStorage statisticsStorage;

	public void setStatisticsStorage(StatisticsStorage statisticsStorage) {
		this.statisticsStorage = statisticsStorage;
	}

	public DubboKeeperMonitorService() {
	}

	@Override
	public void collect(URL statisticsURL) {
		Statistics statistics = new Statistics();
		statistics.setTimestamp(System.currentTimeMillis());
		statistics.setApplication(statisticsURL.getParameter(MonitorService.APPLICATION));
		statistics.setConcurrent(
			Long.valueOf(statisticsURL.getParameter(MonitorService.CONCURRENT, 1)));
		if(statistics.getConcurrent()==0){
			statistics.setConcurrent(Long.valueOf(1));
		}
		statistics.setHost(statisticsURL.getHost());
		statistics.setServiceInterface(statisticsURL.getParameter(MonitorService.INTERFACE));
		statistics.setMethod(statisticsURL.getParameter(MonitorService.METHOD));

        int failureCount = statisticsURL.getParameter(MonitorService.FAILURE,0);
        int successCount = statisticsURL.getParameter(MonitorService.SUCCESS,0);
        statistics.setFailureCount(failureCount);
        statistics.setSuccessCount(successCount);
        int totalCount = failureCount+successCount;
        if(totalCount<=0){
            return;
        }
        statistics.setElapsed(
			Long.valueOf(statisticsURL.getParameter(MonitorService.ELAPSED, 0)/totalCount));
        statistics.setInput(
			Long.valueOf(statisticsURL.getParameter(MonitorService.INPUT,0)/totalCount));
        statistics.setOutput(
			Long.valueOf(statisticsURL.getParameter(MonitorService.OUTPUT,0)/totalCount));
        if(statistics.getElapsed()!=0){
			//TPS=并发数/响应时间
			BigDecimal tps = new BigDecimal(statistics.getConcurrent());
			tps=tps.divide(BigDecimal.valueOf(statistics.getElapsed()),2,BigDecimal.ROUND_HALF_DOWN);
			tps=tps.multiply(BigDecimal.valueOf(1000));
            statistics.setTps(tps.doubleValue());//每秒能够处理的请求数量
        }
		BigDecimal kbps = new BigDecimal(statistics.getTps());
        if(statistics.getInput()!=0&&statistics.getElapsed()!=0){
			//kbps=tps*平均每次传输的数据量
			kbps=kbps.multiply(BigDecimal.valueOf(statistics.getInput()).divide(BigDecimal.valueOf(1024),2,BigDecimal.ROUND_HALF_DOWN));
        }else if(statistics.getElapsed()!=0){
			kbps=kbps.multiply(BigDecimal.valueOf(statistics.getOutput()).divide(BigDecimal.valueOf(1024),2,BigDecimal.ROUND_HALF_DOWN));
        }
		statistics.setKbps(kbps.doubleValue());
		if(statisticsURL.hasParameter(MonitorService.PROVIDER)){
			statistics.setType(Statistics.ApplicationType.CONSUMER);
			statistics.setRemoteType(Statistics.ApplicationType.PROVIDER);
			statistics.setRemoteAddress(statisticsURL.getParameter(MonitorService.PROVIDER));
		}else{
			statistics.setType(Statistics.ApplicationType.PROVIDER);
			statistics.setRemoteType(Statistics.ApplicationType.CONSUMER);
			statistics.setRemoteAddress(statisticsURL.getParameter(MonitorService.CONSUMER));
		}
        statisticsStorage.storeStatistics(statistics);
	}

	
	
	
	@Override
	public List<URL> lookup(URL url) {
		return null;
	}

}
