package com.dubboclub.dk.web.model;

import com.alibaba.dubbo.common.Constants;
import com.dubboclub.dk.admin.model.Override;
import org.apache.commons.lang.StringUtils;

/**
 * Created by bieber on 2015/6/21.
 */
public class OverrideInfo {

    private String application;

    private String parameters;

    private boolean enable;

    private Long id;

    private String address;

    private String serviceKey;


    public Override toOverride(){
        Override override = new Override();
        override.setParams(parameters);
        override.setEnabled(enable);
        override.setApplication(StringUtils.isEmpty(application) ? Constants.ANY_VALUE : application);
        override.setAddress(StringUtils.isEmpty(address) ? Constants.ANYHOST_VALUE : address);
        return override;
    }

    public static OverrideInfo valueOf(Override override){
        OverrideInfo overrideInfo = new OverrideInfo();
        overrideInfo.setAddress(override.getAddress());
        overrideInfo.setApplication(override.getApplication()==null? Constants.ANY_VALUE:override.getApplication());
        overrideInfo.setEnable(override.isEnabled());
        overrideInfo.setId(override.getId());
        overrideInfo.setParameters(override.getParams());
        overrideInfo.setServiceKey(override.getService());
        return overrideInfo;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
