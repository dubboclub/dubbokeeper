package com.dubboclub.dk.storage.model;

import java.io.Serializable;

public class Statistics implements Comparable<Statistics>,Serializable {
	//发生的时间戳
	private Long timestamp;
	private String serviceInterface;
	private String method;
	private ApplicationType type;
    private double tps;
    private double kbps;
	//发生的服务端
	private String host;
	//发生的应用名称
	private String application;
	// 计算调用耗时
	private Long elapsed;
	// 当前并发数
	private Long concurrent;
	//当前请求的输入
	private Long input;
	//当前请求的输出
	private Long output;

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

	public double getTps() {
		return tps;
	}

	public void setTps(double tps) {
		this.tps = tps;
	}

	public double getKbps() {
		return kbps;
	}

	public void setKbps(double kbps) {
		this.kbps = kbps;
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



	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
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


	public Long getElapsed() {
		return elapsed;
	}

	public void setElapsed(Long elapsed) {
		this.elapsed = elapsed;
	}

	public Long getConcurrent() {
		return concurrent;
	}

	public void setConcurrent(Long concurrent) {
		this.concurrent = concurrent;
	}

	public Long getInput() {
		return input;
	}

	public void setInput(Long input) {
		this.input = input;
	}

	public Long getOutput() {
		return output;
	}

	public void setOutput(Long output) {
		this.output = output;
	}

    @Override
    public int compareTo(Statistics o) {
        return this.getTimestamp()>o.getTimestamp()?1:0;
    }

    public static enum ApplicationType{
		CONSUMER,PROVIDER
	}


	 
}
