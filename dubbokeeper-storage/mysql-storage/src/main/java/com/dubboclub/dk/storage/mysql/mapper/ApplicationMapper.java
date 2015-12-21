package com.dubboclub.dk.storage.mysql.mapper;

import com.dubboclub.dk.storage.model.ApplicationInfo;

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
}
