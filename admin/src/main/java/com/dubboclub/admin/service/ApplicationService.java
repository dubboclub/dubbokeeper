package com.dubboclub.admin.service;

import com.dubboclub.admin.model.Application;
import com.dubboclub.admin.model.Node;

import java.util.List;

/**
 * Created by bieber on 2015/6/3.
 */
public interface ApplicationService {

    public List<Application> getApplications();

    public List<Node> getNodesByApplicationName(String appName);
}
