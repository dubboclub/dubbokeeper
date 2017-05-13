package com.dubboclub.dk.storage.mysql;

import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @date: 2015/12/17.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mysql.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext.xml","classpath*:META-INF/spring/mysql.xml"})
public abstract class ApplicationStartUp extends AbstractJUnit4SpringContextTests{


    private TransactionTemplate transactionTemplate;
    @Before
    public void before(){
        transactionTemplate = applicationContext.getBean(TransactionTemplate.class);
    }

    protected <T extends Object> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }


    protected <T extends Object> T doInTransaction(TransactionCallback<T> callback){
        return transactionTemplate.execute(callback);
    }


    protected void printObject(Object object){
        System.out.println(JSON.toJSONString(object));
    }



}
