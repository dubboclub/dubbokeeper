package com.dubboclub.dk.storage.mysql.mapper;

import com.dubboclub.dk.storage.model.ApplicationInfo;
import com.dubboclub.dk.storage.mysql.ApplicationStartUp;
import com.dubboclub.dk.storage.mysql.DBTransactionTestCallback;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionCallback;

/**
 * @date: 2015/12/17.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mysql.mapper.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public class ApplicationMapperTest extends ApplicationStartUp {


    @Autowired
    private ApplicationMapper applicationMapper;

    @Test
    public void testAddApplication() throws Exception {
        final ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setApplicationName("bieber1");

        TransactionCallback<Integer> transactionCallback = new DBTransactionTestCallback<Integer>() {
            @Override
            protected Integer doInTransaction() {
                int result = applicationMapper.addApplication(applicationInfo);
                Assert.assertEquals(1,result);
                return result;
            }
        };
       Integer result = doInTransaction(transactionCallback);
    }
}