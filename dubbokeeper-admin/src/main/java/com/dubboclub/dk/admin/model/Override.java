/*
 * Copyright 1999-2101 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dubboclub.dk.admin.model;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.RegistryConstants;
import org.apache.dubbo.common.utils.StringUtils;

import java.util.Map;

/**
 * @author tony.chenl
 */
public class Override extends BasicModel{
    
    private static final long serialVersionUID = 114828505391757846L;

    private String service;
    
    private String params;
    
    private String application;
    
    private String address;
    
    private String username;
    
    private boolean enabled;
    
    public Override(){
    }

    public Override(long id){
        super(id);
    }
    
    public String getService() {
        return service;
    }

    
    public void setService(String service) {
        this.service = service;
    }

    
    public String getParams() {
        return params;
    }

    
    public void setParams(String params) {
        if(StringUtils.isEmpty(this.params)){
            this.params = params;
        }else{
            this.params +="&"+params;
        }
    }

    
    public String getApplication() {
        return application;
    }

    
    public void setApplication(String application) {
        this.application = application;
    }

    
    public String getAddress() {
        return address;
    }

    
    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    
    public boolean isEnabled() {
        return enabled;
    }

    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String toString() {
        return "Override [service=" + service + ", params=" + params + ", application="
                + application + ", address=" + address + ", username=" + username + ", enabled=" + enabled + "]";
    }
    
    public boolean isDefault() {
    	return (getAddress() == null || getAddress().length() == 0 || CommonConstants.ANY_VALUE.equals(getAddress()) || CommonConstants.ANYHOST_VALUE.equals(getAddress()))
				&& (getApplication() == null || getApplication().length() == 0 || CommonConstants.ANY_VALUE.equals(getApplication()));
    }
    
    public boolean isMatch(String service, String address, String application) {
    	return isEnabled() && getParams() != null && getParams().length() > 0
    			&& service.equals(getService())
    			&& (address == null || getAddress() == null || getAddress().length() == 0 || getAddress().equals(CommonConstants.ANY_VALUE) || getAddress().equals(CommonConstants.ANYHOST_VALUE) || getAddress().equals(address))
    			&& (application == null || getApplication() == null || getApplication().length() == 0 || getApplication().equals(CommonConstants.ANY_VALUE) || getApplication().equals(application));
    }
    
    public boolean isUniqueMatch(Provider provider) {
    	return isEnabled() && getParams() != null && getParams().length() > 0
    			&& provider.getServiceKey().equals(getService())
    			&& provider.getAddress().equals(getAddress());
    }
    
    public boolean isMatch(Provider provider) {
    	return isEnabled() && getParams() != null && getParams().length() > 0
    			&& provider.getServiceKey().equals(getService())
    			&& (getAddress() == null || getAddress().length() == 0 || getAddress().equals(CommonConstants.ANY_VALUE) || getAddress().equals(CommonConstants.ANYHOST_VALUE) || getAddress().equals(provider.getAddress()))
    			&& (getApplication() == null || getApplication().length() == 0 || getApplication().equals(CommonConstants.ANY_VALUE) || getApplication().equals(provider.getApplication()));
    }

    public boolean isUniqueMatch(Consumer consumer) {
    	return isEnabled() && getParams() != null && getParams().length() > 0
    			&& consumer.getServiceKey().equals(getService())
    			&& consumer.getAddress().equals(getAddress());
    }
    
    public boolean isMatch(Consumer consumer) {
    	return isEnabled() && getParams() != null && getParams().length() > 0
    			&& consumer.getServiceKey().equals(getService())
    			&& (getAddress() == null || getAddress().length() == 0 || getAddress().equals(CommonConstants.ANY_VALUE) || getAddress().equals(CommonConstants.ANYHOST_VALUE) || getAddress().equals(consumer.getAddress()))
    			&& (getApplication() == null || getApplication().length() == 0 || getApplication().equals(CommonConstants.ANY_VALUE) || getApplication().equals(consumer.getApplication()));
    }
    
    public Map<String, String> toParametersMap() {
    	Map<String, String> map = StringUtils.parseQueryString(getParams());
    	map.remove(CommonConstants.INTERFACE_KEY);
    	map.remove(CommonConstants.GROUP_KEY);
    	map.remove(CommonConstants.VERSION_KEY);
    	map.remove(CommonConstants.APPLICATION_KEY);
    	map.remove(RegistryConstants.CATEGORY_KEY);
    	map.remove(RegistryConstants.DYNAMIC_KEY);
    	map.remove(CommonConstants.ENABLED_KEY);
    	return map;
    }

    public URL toUrl() {
        String group = null;
        String version = null;
        String path = service;
        int i = path.indexOf("/");
        if (i > 0) {
            group = path.substring(0, i);
            path = path.substring(i + 1);
        }
        i = path.lastIndexOf(":");
        if (i > 0) {
            version = path.substring(i + 1);
            path = path.substring(0, i);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(RegistryConstants.OVERRIDE_PROTOCOL);
        sb.append("://");
        if(! StringUtils.isBlank(address) && ! CommonConstants.ANY_VALUE.equals(address)) {
            sb.append(address);
        } else {
            sb.append(CommonConstants.ANYHOST_VALUE);
        }
        sb.append("/");
        sb.append(path);
        sb.append("?");
        Map<String, String> param = StringUtils.parseQueryString(params);
        param.put(RegistryConstants.CATEGORY_KEY, RegistryConstants.CONFIGURATORS_CATEGORY);
        param.put(CommonConstants.ENABLED_KEY, String.valueOf(isEnabled()));
        param.put(RegistryConstants.DYNAMIC_KEY, "false");
        if(! StringUtils.isBlank(application) && ! CommonConstants.ANY_VALUE.equals(application)) {
            param.put(CommonConstants.APPLICATION_KEY, application);
        }
        if (group != null) {
            param.put(CommonConstants.GROUP_KEY, group);
        }
        if (version != null) {
            param.put(CommonConstants.VERSION_KEY, version);
        }
        sb.append(StringUtils.toQueryString(param));
        return URL.valueOf(sb.toString());
    }

}
