package com.dubboclub.dk.storage.mysql.mapper;

import com.dubboclub.dk.storage.model.ApplicationInfo;
import com.dubboclub.dk.storage.mysql.ApplicationStartUp;
import com.dubboclub.dk.storage.mysql.DBTransactionTestCallback;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionCallback;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * @date: 2015/12/17.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mysql.mapper.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
@Ignore
public class ApplicationMapperTest extends ApplicationStartUp {


    @Autowired
    private ApplicationMapper applicationMapper;

    private String tableTemplate = "CREATE TABLE `statistics_2` (\n" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `timestamp` int(11) NOT NULL DEFAULT '0' COMMENT '时间戳',\n" +
            "  `serviceInterface` varchar(255) NOT NULL DEFAULT '' COMMENT '接口名',\n" +
            "  `method` varchar(255) NOT NULL DEFAULT '' COMMENT '方法名',\n" +
            "  `type` varchar(10) DEFAULT NULL COMMENT '当前调用的应用类型',\n" +
            "  `tps` float(11,2) NOT NULL DEFAULT '0.00' COMMENT 'TPS值',\n" +
            "  `kbps` float(11,2) DEFAULT NULL COMMENT '流量',\n" +
            "  `host` varchar(50) DEFAULT NULL COMMENT 'ip地址',\n" +
            "  `application` varchar(50) DEFAULT NULL COMMENT '应用名',\n" +
            "  `elapsed` int(11) DEFAULT NULL COMMENT '耗时',\n" +
            "  `concurrent` int(11) DEFAULT NULL COMMENT '并发数',\n" +
            "  `input` int(11) DEFAULT NULL COMMENT '输入值',\n" +
            "  `output` int(11) DEFAULT NULL COMMENT '输出大小',\n" +
            "  `successCount` int(11) DEFAULT NULL COMMENT '成功次数',\n" +
            "  `failureCount` int(11) DEFAULT NULL COMMENT '失败次数',\n" +
            "  `remoteAddress` varchar(50) DEFAULT NULL COMMENT '远程地址',\n" +
            "  `remoteType` varchar(20) DEFAULT NULL COMMENT '远程应用类型',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `time-index` (`timestamp`),\n" +
            "  KEY `method-index` (`method`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";

    @Test
    public void testAddApplication() throws Exception {
       /* TransactionCallback<Integer> transactionCallback = new DBTransactionTestCallback<Integer>() {
            @Override
            protected Integer doInTransaction() {
                int result = applicationMapper.addApplication("hello");
                Assert.assertEquals(1,result);
                return result;
            }
        };
       Integer result = doInTransaction(transactionCallback);*/
    }


}