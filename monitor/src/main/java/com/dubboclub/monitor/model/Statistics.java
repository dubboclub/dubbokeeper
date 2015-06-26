package com.dubboclub.monitor.model;

import java.util.Date;

public class Statistics {
	Date timestamp;
	String serviceInterface;
	String method;
	String type; // consumer/provider
	String provider;
	String consumer;
	long success;
	long failure;
	long elapsed;
	long concurrent;
	long maxConcurrent;
	long maxElapsed;
	long maxInput;
	long maxOutput;

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public long getSuccess() {
		return success;
	}

	public void setSuccess(long success) {
		this.success = success;
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

	public long getMaxConcurrent() {
		return maxConcurrent;
	}

	public void setMaxConcurrent(long maxConcurrent) {
		this.maxConcurrent = maxConcurrent;
	}

	public long getMaxElapsed() {
		return maxElapsed;
	}

	public void setMaxElapsed(long maxElapsed) {
		this.maxElapsed = maxElapsed;
	}

	public long getMaxInput() {
		return maxInput;
	}

	public void setMaxInput(long maxInput) {
		this.maxInput = maxInput;
	}

	public long getMaxOutput() {
		return maxOutput;
	}

	public void setMaxOutput(long maxOutput) {
		this.maxOutput = maxOutput;
	}

	public long getFailure() {
		return failure;
	}

	public void setFailure(long failure) {
		this.failure = failure;
	}
}
