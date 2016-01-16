package com.dubboclub.dk.storage.mysql.mapper;

import com.dubboclub.dk.storage.model.ApplicationInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @date: 2015/12/17.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mysql.mapper.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public interface ApplicationMapper {

    public int addApplication(ApplicationInfo applicationInfo);

    public List<ApplicationInfo> listApps();


    public int getAppType(@Param("name")String name);

    public int updateAppType(@Param("name")String name,@Param("type")int type);

}
