package com.dubboclub.dk.storage.mysql.mapper;

import com.dubboclub.dk.storage.model.ServiceInfo;
import com.dubboclub.dk.storage.model.Statistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @date: 2015/12/28.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mysql.mapper.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public interface StatisticsMapper {

    public Integer addOne(@Param("application")String application,@Param("statistics")Statistics statistics);

    public Integer batchInsert(@Param("application")String application,@Param("list")List<Statistics> statistics);

    public Long queryMaxElapsed(@Param("application")String application,@Param("service")String service,@Param("start")long start,@Param("end")long end);

    public Long queryMaxConcurrent(@Param("application")String application,@Param("service")String service,@Param("start")long start,@Param("end")long end);

    public Integer queryMaxFault(@Param("application")String application,@Param("service")String service,@Param("start")long start,@Param("end")long end);

    public Integer queryMaxSuccess(@Param("application")String application,@Param("service")String service,@Param("start")long start,@Param("end")long end);

    public List<Statistics> queryStatisticsForMethod(@Param("application")String application,@Param("start")long start,@Param("end")long end,@Param("serviceInterface")String serviceInterface,@Param("method")String method);

    public Long queryMethodMaxItemByServiceForLong(@Param("item")String item,@Param("application")String application,@Param("serviceInterface")String serviceInterface,@Param("method")String method,@Param("start")long start,@Param("end")long end);

    public Integer queryMethodMaxItemByServiceForInteger(@Param("item")String item,@Param("application")String application,@Param("serviceInterface")String serviceInterface,@Param("method")String method,@Param("start")long start,@Param("end")long end);

    public Double queryMethodMaxItemByServiceForDouble(@Param("item")String item,@Param("application")String application,@Param("serviceInterface")String serviceInterface,@Param("method")String method,@Param("start")long start,@Param("end")long end);

    public List<String> queryMethodForService(@Param("application")String application,@Param("serviceInterface")String serviceInterface);

    public List<Statistics> queryApplicationOverview(@Param("application")String application,@Param("item")String item,@Param("start")long start,@Param("end")long end);


    public List<Statistics> queryServiceOverview(@Param("application")String application,@Param("service")String service,@Param("item")String item,@Param("start")long start,@Param("end")long end);

    public List<ServiceInfo> queryServiceByApp(@Param("application")String application);

}
