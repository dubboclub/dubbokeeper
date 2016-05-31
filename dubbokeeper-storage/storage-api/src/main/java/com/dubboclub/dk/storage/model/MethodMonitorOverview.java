package com.dubboclub.dk.storage.model;

import java.io.Serializable;

/**
 * Created by bieber on 2015/9/29.
 */
public class MethodMonitorOverview implements Serializable {

    private String method;

    private Long maxElapsed;

    private Long maxConcurrent;

    private Long maxInput;

    private Long minInput;

    private Long maxOutput;

    private Long minOutput;

    private Long minConcurrent;

    private Long minElapsed;

    private int minFailure;

    private int maxFailure;

    private int minSuccess;

    private int maxSuccess;

    private double maxTps;

    private double minTps;

    private double maxKbps;

    private double minKbps;

    public int getMinFailure() {
        return minFailure;
    }

    public void setMinFailure(int minFailure) {
        this.minFailure = minFailure;
    }

    public int getMaxFailure() {
        return maxFailure;
    }

    public void setMaxFailure(int maxFailure) {
        this.maxFailure = maxFailure;
    }

    public int getMinSuccess() {
        return minSuccess;
    }

    public void setMinSuccess(int minSuccess) {
        this.minSuccess = minSuccess;
    }

    public int getMaxSuccess() {
        return maxSuccess;
    }

    public void setMaxSuccess(int maxSuccess) {
        this.maxSuccess = maxSuccess;
    }

    public double getMaxTps() {
        return maxTps;
    }

    public void setMaxTps(double maxTps) {
        this.maxTps = maxTps;
    }

    public double getMinTps() {
        return minTps;
    }

    public void setMinTps(double minTps) {
        this.minTps = minTps;
    }

    public double getMaxKbps() {
        return maxKbps;
    }

    public void setMaxKbps(double maxKbps) {
        this.maxKbps = maxKbps;
    }

    public double getMinKbps() {
        return minKbps;
    }

    public void setMinKbps(double minKbps) {
        this.minKbps = minKbps;
    }

    public Long getMinInput() {
        return minInput;
    }

    public void setMinInput(Long minInput) {
        this.minInput = minInput;
    }

    public Long getMinOutput() {
        return minOutput;
    }

    public void setMinOutput(Long minOutput) {
        this.minOutput = minOutput;
    }

    public Long getMinConcurrent() {
        return minConcurrent;
    }

    public void setMinConcurrent(Long minConcurrent) {
        this.minConcurrent = minConcurrent;
    }

    public Long getMinElapsed() {
        return minElapsed;
    }

    public void setMinElapsed(Long minElapsed) {
        this.minElapsed = minElapsed;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getMaxElapsed() {
        return maxElapsed;
    }

    public void setMaxElapsed(Long maxElapsed) {
        this.maxElapsed = maxElapsed;
    }

    public Long getMaxConcurrent() {
        return maxConcurrent;
    }

    public void setMaxConcurrent(Long maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
    }

    public Long getMaxInput() {
        return maxInput;
    }

    public void setMaxInput(Long maxInput) {
        this.maxInput = maxInput;
    }

    public Long getMaxOutput() {
        return maxOutput;
    }

    public void setMaxOutput(Long maxOutput) {
        this.maxOutput = maxOutput;
    }

}
