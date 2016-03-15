package com.dubboclub.dk.storage.mongodb.dto;

import java.io.Serializable;

/**
 * Created by hideh on 2016/3/15.
 */
public class TempMethodOveride implements Serializable{

    private  String m;
    private int total;

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
