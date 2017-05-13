package com.dubboclub.dk.web.model;

/**
 * Created by bieber on 2015/6/12.
 */
public class BasicResponse {

    public static final short SUCCESS=0,FAILED=1;

    private short result=SUCCESS;

    private String memo;

    public short getResult() {
        return result;
    }

    public void setResult(short result) {
        this.result = result;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
