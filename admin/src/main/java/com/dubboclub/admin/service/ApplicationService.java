package com.dubboclub.admin.service;

import com.dubboclub.admin.model.Application;
import com.dubboclub.admin.model.Node;

import java.util.List;

/**
 * Created by bieber on 2015/6/3.
 */
public interface ApplicationService {

    //获取当前注册中心所有应用列表
    public List<Application> getApplications();
    //获取某个应用部署节点信息
    public List<Node> getNodesByApplicationName(String appName);
}
