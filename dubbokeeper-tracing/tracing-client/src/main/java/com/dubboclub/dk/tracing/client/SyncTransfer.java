package com.dubboclub.dk.tracing.client;


import com.alibaba.dubbo.common.extension.SPI;
import com.dubboclub.dk.tracing.api.Span;

/**
 * Created by Zetas on 2016/7/8.
 */
@SPI("default")
public interface SyncTransfer {

    public void start();

    public void cancel();

    public void syncSend(Span span);

}
