package com.dubboclub.dk.web.model;


import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.StringUtils;

import java.util.Map;

/**
 * Created by bieber on 2015/6/21.
 */
public class LoadBalanceOverrideInfo extends OverrideInfo {

    private String loadbalance;

    private String methods;

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }

    public String getLoadbalance() {
        return loadbalance;
    }

    public void setLoadbalance(String loadbalance) {
        this.loadbalance = loadbalance;
    }

    public static LoadBalanceOverrideInfo valueOf(com.dubboclub.dk.admin.model.Override override){
        String loadBalance = null;
        String methods = null;
        if(!org.apache.commons.lang.StringUtils.isEmpty(override.getParams())){
            Map<String,String> parameters = StringUtils.parseQueryString(override.getParams());
            loadBalance=parameters.get(Constants.LOADBALANCE_KEY);
            methods=parameters.get(Constants.METHODS_KEY)==null? Constants.ANY_VALUE:parameters.get(Constants.METHODS_KEY);
            if(org.apache.commons.lang.StringUtils.isEmpty(loadBalance)){
                return null;
            }
        }
        LoadBalanceOverrideInfo overrideInfo = new LoadBalanceOverrideInfo();
        overrideInfo.setAddress(override.getAddress());
        overrideInfo.setApplication(override.getApplication()==null?Constants.ANY_VALUE:override.getApplication());
        overrideInfo.setEnable(override.isEnabled());
        overrideInfo.setId(override.getId());
        overrideInfo.setParameters(override.getParams());
        overrideInfo.setLoadbalance(loadBalance);
        overrideInfo.setMethods(methods);
        return overrideInfo;
    }
}
