package com.dubboclub.dk.storage.mongodb;

/**
 * @date: 2015/12/14.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mongodb.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public class MongodbConfigurer {

    private String dbName;

    private String connects;


    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getConnects() {
        return connects;
    }

    public void setConnects(String connects) {
        this.connects = connects;
    }

}
