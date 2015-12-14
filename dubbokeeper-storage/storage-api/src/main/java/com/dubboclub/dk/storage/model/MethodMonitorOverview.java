package com.dubboclub.dk.storage.model;

/**
 * Created by bieber on 2015/9/29.
 */
public class MethodMonitorOverview {

    private String method;

    private long maxElapsed;

    private long maxConcurrent;

    private long maxInput;

    private long minInput;

    private long maxOutput;

    private long minOutput;

    private long minConcurrent;

    private long minElapsed;

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

    public long getMinInput() {
        return minInput;
    }

    public void setMinInput(long minInput) {
        this.minInput = minInput;
    }

    public long getMinOutput() {
        return minOutput;
    }

    public void setMinOutput(long minOutput) {
        this.minOutput = minOutput;
    }

    public long getMinConcurrent() {
        return minConcurrent;
    }

    public void setMinConcurrent(long minConcurrent) {
        this.minConcurrent = minConcurrent;
    }

    public long getMinElapsed() {
        return minElapsed;
    }

    public void setMinElapsed(long minElapsed) {
        this.minElapsed = minElapsed;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public long getMaxElapsed() {
        return maxElapsed;
    }

    public void setMaxElapsed(long maxElapsed) {
        this.maxElapsed = maxElapsed;
    }

    public long getMaxConcurrent() {
        return maxConcurrent;
    }

    public void setMaxConcurrent(long maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
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

}
