package com.dubboclub.web.model;

import com.alibaba.dubbo.common.Constants;
import com.dubboclub.admin.model.*;

/**
 * Created by bieber on 2015/6/21.
 */
public class OverrideInfo {

    private String application;

    private String parameters;

    private boolean enable;

    private long id;

    private String address;


    public static OverrideInfo valueOf(com.dubboclub.admin.model.Override override){
        OverrideInfo overrideInfo = new OverrideInfo();
        overrideInfo.setAddress(override.getAddress());
        overrideInfo.setApplication(override.getApplication()==null? Constants.ANY_VALUE:override.getApplication());
        overrideInfo.setEnable(override.isEnabled());
        overrideInfo.setId(override.getId());
        overrideInfo.setParameters(override.getParams());
        return overrideInfo;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
