package com.dubboclub.admin.service;

import com.dubboclub.admin.model.Provider;

import java.util.List;

/**
 * Created by bieber on 2015/6/3.
 */
public interface ProviderService {


    //查询某个应用提供的所有服务信息
    public List<Provider> listProviderByApplication(String appName);

    //通过服务查询其提供者信息
    public List<Provider> listProviderByService(String service);

    //通过多条件查询符合全部条件的提供者信息
    public List<Provider> listProviderByConditions(String...conditions);

    //通过ID查询某个提供者
    public Provider getProviderById(long id);

    //更新提供者配置信息
    public void updateProvider(Provider newProvider);


}
