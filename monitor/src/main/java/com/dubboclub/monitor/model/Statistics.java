package com.dubboclub.monitor.model;

import org.springframework.context.ApplicationContextAware;

import java.util.Date;

public class Statistics {
	//发生的时间戳
	private long timestamp;
	private String serviceInterface;
	private String method;
	private ApplicationType type;

    private long tps;

    private long kbps;
	//发生的服务端
	private String host;
	//发生的应用名称
	private String application;
	// 计算调用耗时
	private long elapsed;
	// 当前并发数
	private long concurrent;
	//当前请求的输入
	private long input;
	//当前请求的输出
	private long output;

    private int successCount;

    private int failureCount;

	//调用的远程地址
	private String remoteAddress;
	//调用的远程应用类型
	private ApplicationType remoteType;

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public long getKbps() {
        return kbps;
    }

    public void setKbps(long kbps) {
        this.kbps = kbps;
    }

    public long getTps() {
        return tps;
    }

    public void setTps(long tps) {
        this.tps = tps;
    }

    public ApplicationType getRemoteType() {
		return remoteType;
	}

	public void setRemoteType(ApplicationType remoteType) {
		this.remoteType = remoteType;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}



	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getServiceInterface() {
		return serviceInterface;
	}

	public void setServiceInterface(String serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public ApplicationType getType() {
		return type;
	}

	public void setType(ApplicationType type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}


	public long getElapsed() {
		return elapsed;
	}

	public void setElapsed(long elapsed) {
		this.elapsed = elapsed;
	}

	public long getConcurrent() {
		return concurrent;
	}

	public void setConcurrent(long concurrent) {
		this.concurrent = concurrent;
	}

	public long getInput() {
		return input;
	}

	public void setInput(long input) {
		this.input = input;
	}

	public long getOutput() {
		return output;
	}

	public void setOutput(long output) {
		this.output = output;
	}

	public static enum ApplicationType{
		CONSUMER,PROVIDER
	}
	 
}
