package com.dubboclub.dk.admin.service.impl;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.RegistryConstants;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.cluster.Constants;

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
        }, RegistryConstants.PROVIDERS_CATEGORY);
    }

    public List<Provider> listProviderByApplication(String appName) {
        return filterCategoryData(new ConvertURL2Entity<Provider>() {
            public Provider convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Provider(pair);
            }
        },RegistryConstants.PROVIDERS_CATEGORY,CommonConstants.APPLICATION_KEY,appName);
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
        },RegistryConstants.PROVIDERS_CATEGORY,CommonConstants.INTERFACE_KEY,service);
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
        },RegistryConstants.PROVIDERS_CATEGORY,conditions);
    }

    @java.lang.Override
    public List<Provider> listProviderByServiceKey(String serviceKey) {
        return listProviderByConditions(CommonConstants.INTERFACE_KEY, Tool.getInterface(serviceKey),CommonConstants.GROUP_KEY,Tool.getGroup(serviceKey),CommonConstants.VERSION_KEY,Tool.getVersion(serviceKey));
    }

    public Provider getProviderById(long id) {
        URL url = getOneById(RegistryConstants.PROVIDERS_CATEGORY,id);
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
            if(params.containsKey(CommonConstants.ENABLED_KEY)&&!Boolean.parseBoolean(params.get(CommonConstants.ENABLED_KEY))){
                override.setParams(CommonConstants.DISABLED_KEY + "=true");
            }else{
                override.setParams(CommonConstants.DISABLED_KEY + "=false");
            }
            overrideService.add(override);
            List<Override> overrides = overrideService.listByProvider(oldProvider);
            URL editOverrideUrl = override.toUrl();
            for(Override item:overrides){
                URL overrideUrl = item.toUrl();
                if(overrideUrl.getParameter(CommonConstants.DISABLED_KEY,false)!=editOverrideUrl.getParameter(CommonConstants.DISABLED_KEY,false)){
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
        override.setParams(CommonConstants.ANYHOST_KEY+"="+params.get(CommonConstants.ANYHOST_KEY));
        override.setParams(CommonConstants.APPLICATION_KEY+"="+CommonConstants.ANY_VALUE);
        if(!StringUtils.isEmpty(params.get(CommonConstants.GROUP_KEY))){
            override.setParams(CommonConstants.GROUP_KEY+"="+params.get(CommonConstants.GROUP_KEY));
        }
        if(!StringUtils.isEmpty(params.get(CommonConstants.VERSION_KEY))){
            override.setParams(CommonConstants.VERSION_KEY+"="+ params.get(CommonConstants.VERSION_KEY));
        }
        override.setParams("owner="+params.get("owner"));
        return override;
    }

    @java.lang.Override
    public void disable(Long id) {
        Provider provider = getProviderById(id);
        if(provider.isDynamic()){
            URL url = overrideService.configProviderURL(provider);
            url=url.addParameter(CommonConstants.ENABLED_KEY, false);
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
            url=url.addParameter(CommonConstants.ENABLED_KEY, true);
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
        url=url.addParameter(CommonConstants.TIMESTAMP_KEY,System.currentTimeMillis());
        add(url);
    }

    public void setOverrideService(OverrideService overrideService) {
        this.overrideService = overrideService;
    }
}
