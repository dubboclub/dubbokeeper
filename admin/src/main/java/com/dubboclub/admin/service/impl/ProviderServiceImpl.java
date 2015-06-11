package com.dubboclub.admin.service.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.dubboclub.admin.model.Consumer;
import com.dubboclub.admin.model.Provider;
import com.dubboclub.admin.service.AbstractService;
import com.dubboclub.admin.service.ProviderService;
import com.dubboclub.admin.sync.util.Pair;
import com.dubboclub.admin.sync.util.SyncUtils;
import com.sun.corba.se.impl.orbutil.closure.Constant;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by bieber on 2015/6/3.
 */
public class ProviderServiceImpl extends AbstractService implements ProviderService {


    @Override
    public List<Provider> listProviderByApplication(String appName) {
        return filterCategoryData(new ConvertURL2Entity<Provider>() {
            @Override
            public Provider convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Provider(pair);
            }
        },Constants.PROVIDERS_CATEGORY,Constants.APPLICATION_KEY,appName);
    }

    @Override
    public List<Provider> listProviderByService(String service) {
        return filterCategoryData(new ConvertURL2Entity<Provider>() {
            @Override
            public Provider convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Provider(pair);
            }
        },Constants.PROVIDERS_CATEGORY,Constants.INTERFACE_KEY,service);
    }

    @Override
    public List<Provider> listProviderByConditions(String... conditions) {
        return filterCategoryData(new ConvertURL2Entity<Provider>() {
            @Override
            public Provider convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Provider(pair);
            }
        },Constants.PROVIDERS_CATEGORY,conditions);
    }

    @Override
    public Provider getProviderById(long id) {
        URL url = getOneById(Constants.PROVIDERS_CATEGORY,id);
        if(url!=null){
            return SyncUtils.url2Provider(new Pair<Long, URL>(id,url));
        }
        return null;
    }

    @Override
    public void updateProvider(Provider newProvider) {
        URL oldURL = getOneById(Constants.PROVIDERS_CATEGORY,newProvider.getId());
        URL newURL = SyncUtils.provider2URL(newProvider);
        update(oldURL,newURL);
    }


}
