package com.dubboclub.admin.service;

import com.dubboclub.admin.model.Provider;

import java.util.List;

/**
 * Created by bieber on 2015/6/3.
 */
public interface ProviderService {



    public List<Provider> listProviderByApplication(String appName);


    public List<Provider> listProviderByService(String service);


}
