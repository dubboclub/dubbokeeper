package com.dubboclub.dk.admin.service.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.dubboclub.dk.admin.model.Override;
import com.dubboclub.dk.admin.model.Provider;
import com.dubboclub.dk.admin.service.AbstractService;
import com.dubboclub.dk.admin.service.OverrideService;
import com.dubboclub.dk.admin.service.ProviderService;
import com.dubboclub.dk.admin.sync.util.Pair;
import com.dubboclub.dk.admin.sync.util.SyncUtils;
import com.dubboclub.dk.admin.sync.util.Tool;

import java.util.*;

/**
 * Created by bieber on 2015/6/3.
 */
public class ProviderServiceImpl extends AbstractService implements ProviderService {


    private OverrideService overrideService;

    @java.lang.Override
    public List<Provider> listAllProvider() {
        return filterCategoryData(new ConvertURL2Entity<Provider>() {
            public Provider convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Provider(pair);
            }
        },Constants.PROVIDERS_CATEGORY);
    }

    public List<Provider> listProviderByApplication(String appName) {
        return filterCategoryData(new ConvertURL2Entity<Provider>() {
            public Provider convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Provider(pair);
            }
        },Constants.PROVIDERS_CATEGORY,Constants.APPLICATION_KEY,appName);
    }

    public List<Provider> listProviderByService(String service) {
        return filterCategoryData(new ConvertURL2Entity<Provider>() {
            List<String> hadContained = new ArrayList<String>();
            public Provider convert(Pair<Long, URL> pair) {
                if(hadContained.contains(pair.getValue().getHost()+":"+pair.getValue().getPort())){
                    return null;
                }
                hadContained.add(pair.getValue().getHost()+":"+pair.getValue().getPort());
                return SyncUtils.url2Provider(pair);
            }
        },Constants.PROVIDERS_CATEGORY,Constants.INTERFACE_KEY,service);
    }

    public List<Provider> listProviderByConditions(String... conditions) {
        return filterCategoryData(new ConvertURL2Entity<Provider>() {
            public Provider convert(Pair<Long, URL> pair) {
                Provider provider = SyncUtils.url2Provider(pair);
                if(provider.isDynamic()){
                    return overrideService.configProvider(provider);
                }
                return provider;
            }
        },Constants.PROVIDERS_CATEGORY,conditions);
    }

    @java.lang.Override
    public List<Provider> listProviderByServiceKey(String serviceKey) {
        return listProviderByConditions(Constants.INTERFACE_KEY, Tool.getInterface(serviceKey),Constants.GROUP_KEY,Tool.getGroup(serviceKey),Constants.VERSION_KEY,Tool.getVersion(serviceKey));
    }

    public Provider getProviderById(long id) {
        URL url = getOneById(Constants.PROVIDERS_CATEGORY,id);
        if(url!=null){
            Provider provider = SyncUtils.url2Provider(new Pair<Long, URL>(id, url));
            if(provider.isDynamic()){
                return overrideService.configProvider(provider);
            }else{
                return provider;
            }

        }
        return null;
    }

    public void updateProvider(Provider newProvider) {
        Provider oldProvider = getProviderById(newProvider.getId());
        if(newProvider.isDynamic()){
            Map<String,String> params = Tool.convertParametersMap(newProvider.getParameters());
            Override override = generateDefaultOverride(newProvider);
            if(params.containsKey(Constants.ENABLED_KEY)&&!Boolean.parseBoolean(params.get(Constants.ENABLED_KEY))){
                override.setParams(Constants.DISABLED_KEY + "=true");
            }else{
                override.setParams(Constants.DISABLED_KEY + "=false");
            }
            overrideService.add(override);
            List<Override> overrides = overrideService.listByProvider(oldProvider);
            URL editOverrideUrl = override.toUrl();
            for(Override item:overrides){
                URL overrideUrl = item.toUrl();
                if(overrideUrl.getParameter(Constants.DISABLED_KEY,false)!=editOverrideUrl.getParameter(Constants.DISABLED_KEY,false)){
                    overrideService.delete(item.getId());
                }else if(!StringUtils.isEmpty(params.get(Constants.WEIGHT_KEY))&&!params.get(Constants.WEIGHT_KEY).equals(overrideUrl.getParameter(Constants.WEIGHT_KEY))){
                    overrideService.delete(item.getId());
                }
            }

        }else{
            URL newURL = SyncUtils.provider2URL(newProvider);
            update(SyncUtils.provider2URL(oldProvider),newURL);
        }
    }


    private Override generateDefaultOverride(Provider provider){
        Map<String,String> params = Tool.convertParametersMap(provider.getParameters());
        Override override = new Override();
        override.setAddress(provider.getAddress());
        override.setService(Tool.getInterface(provider.getServiceKey()));
        override.setEnabled(true);
        if(!StringUtils.isEmpty(params.get(Constants.WEIGHT_KEY))){
            override.setParams(Constants.WEIGHT_KEY+"="+params.get(Constants.WEIGHT_KEY));
        }
        override.setParams(Constants.ANYHOST_KEY+"="+params.get(Constants.ANYHOST_KEY));
        override.setParams(Constants.APPLICATION_KEY+"="+Constants.ANY_VALUE);
        if(!StringUtils.isEmpty(params.get(Constants.GROUP_KEY))){
            override.setParams(Constants.GROUP_KEY+"="+params.get(Constants.GROUP_KEY));
        }
        if(!StringUtils.isEmpty(params.get(Constants.VERSION_KEY))){
            override.setParams(Constants.VERSION_KEY+"="+ params.get(Constants.VERSION_KEY));
        }
        override.setParams("owner="+params.get("owner"));
        return override;
    }

    @java.lang.Override
    public void disable(Long id) {
        Provider provider = getProviderById(id);
        if(provider.isDynamic()){
            URL url = overrideService.configProviderURL(provider);
            url=url.addParameter(Constants.ENABLED_KEY, false);
            updateProvider(SyncUtils.url2Provider(new Pair<Long, URL>(id,url)));
        }else{
            provider.setEnabled(false);
            updateProvider(provider);
        }

    }

    @java.lang.Override
    public void enable(Long id) {
        Provider provider = getProviderById(id);
        if(provider.isDynamic()){
            URL url = overrideService.configProviderURL(provider);
            url=url.addParameter(Constants.ENABLED_KEY, true);
            updateProvider(SyncUtils.url2Provider(new Pair<Long, URL>(id,url)));
        }else{
            provider.setEnabled(true);
            updateProvider(provider);
        }

    }

    @java.lang.Override
    public void halfWeight(Long id) {
        Provider provider = getProviderById(id);
        if(provider.isDynamic()){
            URL url = overrideService.configProviderURL(provider);
            url=url.addParameter(Constants.WEIGHT_KEY, (int)(url.getParameter(Constants.WEIGHT_KEY,Constants.DEFAULT_WEIGHT)/2));
            updateProvider(SyncUtils.url2Provider(new Pair<Long, URL>(id,url)));
        }else{
            provider.setWeight(provider.getWeight()/2);
            updateProvider(provider);
        }

    }

    @java.lang.Override
    public void doubleWeight(Long id) {
        Provider provider = getProviderById(id);
        if(provider.isDynamic()){
            URL url = overrideService.configProviderURL(provider);
            url=url.addParameter(Constants.WEIGHT_KEY,  url.getParameter(Constants.WEIGHT_KEY,Constants.DEFAULT_WEIGHT)*2);
            updateProvider(SyncUtils.url2Provider(new Pair<Long, URL>(id,url)));
        }else{
            provider.setWeight(provider.getWeight()*2);
            updateProvider(provider);
        }

    }

    @java.lang.Override
    public void delete(Long id) {
        Provider provider =  getProviderById(id);
        if(!provider.isDynamic()){
            delete(SyncUtils.provider2URL(provider));
        }
    }

    @java.lang.Override
    public void copy(Long id) {
        Provider provider = getProviderById(id);
        if(provider.isDynamic()){
            provider.setDynamic(false);
        }
        provider.setEnabled(false);
        URL url = SyncUtils.provider2URL(provider);
        url=url.addParameter(Constants.TIMESTAMP_KEY,System.currentTimeMillis());
        add(url);
    }

    public void setOverrideService(OverrideService overrideService) {
        this.overrideService = overrideService;
    }
}
