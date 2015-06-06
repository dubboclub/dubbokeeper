package com.dubboclub.admin.service.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
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
        Map<String,String> filter = new HashMap<String,String>();
        filter.put(Constants.APPLICATION_KEY,appName);
        Collection<Map.Entry<Long,URL>> urls = filterCategoryData(filter,Constants.PROVIDERS_CATEGORY);
        List<Provider> providers = new ArrayList<Provider>();
        for(Map.Entry<Long,URL> url:urls){
            providers.add(SyncUtils.url2Provider(new Pair<Long, URL>(url)));
        }
        return providers;
    }

    @Override
    public List<Provider> listProviderByService(String service) {
        Map<String,String> filter = new HashMap<String,String>();
        filter.put(Constants.INTERFACE_KEY,service);
        Collection<Map.Entry<Long,URL>> urls = filterCategoryData(filter,Constants.PROVIDERS_CATEGORY);
        List<Provider> providers = new ArrayList<Provider>();
        for(Map.Entry<Long,URL> url:urls){
            providers.add(SyncUtils.url2Provider(new Pair<Long, URL>(url)));
        }
        return providers;
    }


}
