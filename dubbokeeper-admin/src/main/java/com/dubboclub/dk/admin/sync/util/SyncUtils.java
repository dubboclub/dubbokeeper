/**
 * Project: dubbo.governance-2.2.0-SNAPSHOT
 * 
 * File Created at Mar 31, 2012
 * $Id: SyncUtils.java 184666 2012-07-05 11:13:17Z tony.chenl $
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.dubboclub.dk.admin.sync.util;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.cluster.Configurator;
import com.alibaba.dubbo.rpc.cluster.ConfiguratorFactory;
import com.dubboclub.dk.admin.model.Override;
import com.dubboclub.dk.admin.model.*;

import java.util.*;

/**
 * @author ding.lid
 */
public class SyncUtils {
    
    public static final String SERVICE_FILTER_KEY = ".service";

    public static final String ADDRESS_FILTER_KEY = ".address";
    
    public static final String ID_FILTER_KEY = ".id";



    private static final ConfiguratorFactory configuratorFactory = ExtensionLoader.getExtensionLoader(ConfiguratorFactory.class).getAdaptiveExtension();

    public static String generateServiceKey(URL url){
        String inf = url.getServiceInterface();
        if (inf == null) return null;
        StringBuilder buf = new StringBuilder();
        String group = url.getParameter(Constants.GROUP_KEY);
        if (group != null&& group.length() > 0) {
            buf.append(group).append("/");
        }
        buf.append(inf);
        String version = url.getParameter(Constants.VERSION_KEY);
        if (version != null&& version.length() > 0) {
            buf.append(":").append(version);
        }
        return buf.toString();
    }

    public static URL provider2URL(Provider provider){
        URL url = URL.valueOf(provider.getUrl());
        url=url.addParameterString(provider.getParameters());
        url = url.addParameter(Constants.WEIGHT_KEY,provider.getWeight());
        url=url.addParameter(Constants.ENABLED_KEY,provider.isEnabled());
        url=url.addParameter(Constants.DYNAMIC_KEY,provider.isDynamic());
        return url;
    }

    public static Configurator toConfigurators(URL url){
        if (Constants.EMPTY_PROTOCOL.equals(url.getProtocol())) {
            return null;
        }
        Map<String,String> override = new HashMap<String, String>(url.getParameters());
        //override 上的anyhost可能是自动添加的，不能影响改变url判断
        override.remove(Constants.ANYHOST_KEY);
        if (override.size() == 0){
            return null;
        }
        return configuratorFactory.getConfigurator(url);

    }

    public static Provider url2Provider(Pair<Long, URL> pair) {
    	if (pair == null) {
    		return null;
    	}
    	
        Long id = pair.getKey();
        URL url = pair.getValue();

        if (url == null)
            return null;

        Provider p = new Provider();
        p.setId(id);
        p.setServiceKey(generateServiceKey(url));
        p.setAddress(url.getAddress());
        p.setApplication(url.getParameter(Constants.APPLICATION_KEY));
        p.setUrl(url.toIdentityString());
        p.setParameters(url.toParameterString());

        p.setDynamic(url.getParameter("dynamic", true));
        p.setEnabled(url.getParameter(Constants.ENABLED_KEY, true));
        if(!url.getParameters().containsKey(Constants.WEIGHT_KEY)){
            p.setWeight(Constants.DEFAULT_WEIGHT);
        }else{
            p.setWeight("null".equals(url.getParameter(Constants.WEIGHT_KEY))?Constants.DEFAULT_WEIGHT:Integer.parseInt(url.getParameter(Constants.WEIGHT_KEY)));
        }
        p.setUsername(url.getParameter("owner"));
        p.setGroup(url.getParameter(Constants.GROUP_KEY));
        p.setVersion(url.getParameter(Constants.VERSION_KEY));
        return p;
    }
    
    public static List<Provider> url2ProviderList(Map<Long, URL> ps) {
        List<Provider> ret = new ArrayList<Provider>();
        for(Map.Entry<Long, URL> entry : ps.entrySet()) {
            ret.add(url2Provider(new Pair<Long, URL>(entry.getKey(), entry.getValue())));
        }
        return ret;
    }

    public static Consumer url2Consumer(Pair<Long, URL> pair) {
    	if (pair == null) {
    		return null;
    	}
    	
        Long id = pair.getKey();
        URL url = pair.getValue();

        if (null == url)
            return null;

        Consumer c = new Consumer();
        c.setId(id);
        c.setServiceKey(generateServiceKey(url));
        c.setAddress(url.getHost());
        c.setApplication(url.getParameter(Constants.APPLICATION_KEY));
        c.setParameters(url.toParameterString());
        c.setUsername(url.getParameter("owner"));
        c.setGroup(url.getParameter(Constants.GROUP_KEY));
        c.setVersion(url.getParameter(Constants.VERSION_KEY));
        return c;
    }
    
    public static List<Consumer> url2ConsumerList(Map<Long, URL> cs) {
        List<Consumer> list = new ArrayList<Consumer>();
        if(cs == null) return list;
        for(Map.Entry<Long, URL> entry : cs.entrySet()) {
            list.add(url2Consumer(new Pair<Long, URL>(entry.getKey(), entry.getValue())));
        }
        return list;
    }

    public static Route url2Route(Pair<Long, URL> pair) {
    	if (pair == null) {
    		return null;
    	}
    	
        Long id = pair.getKey();
        URL url = pair.getValue();

        if (null == url)
            return null;

        Route r = new Route();
        r.setId(id);
        r.setName(url.getParameter("name"));
        r.setService(generateServiceKey(url));
        r.setPriority(url.getParameter(Constants.PRIORITY_KEY, 0));
        r.setEnabled(url.getParameter(Constants.ENABLED_KEY, true));
        r.setForce(url.getParameter(Constants.FORCE_KEY, false));
        r.setType(url.getParameter(Constants.ROUTER_KEY));
        r.setRule(url.getParameterAndDecoded(Constants.RULE_KEY));
        r.setScriptType(url.getParameter(Constants.TYPE_KEY,Constants.DEFAULT_SCRIPT_TYPE_KEY));
        return r;
    }
    
    public static List<Route> url2RouteList(Map<Long, URL> cs) {
        List<Route> list = new ArrayList<Route>();
        if(cs == null) return list;
        for(Map.Entry<Long, URL> entry : cs.entrySet()) {
            list.add(url2Route(new Pair<Long, URL>(entry.getKey(), entry.getValue())));
        }
        return list;
    }

    public static com.dubboclub.dk.admin.model.Override url2Override(Pair<Long, URL> pair) {
    	if (pair == null) {
    		return null;
    	}
    	
        Long id = pair.getKey();
        URL url = pair.getValue();

        if (null == url)
            return null;

        Override o = new Override();
        o.setId(id);

        Map<String, String> parameters = new HashMap<String, String>(url.getParameters());

        o.setService(generateServiceKey(url));
        parameters.remove(Constants.INTERFACE_KEY);
        parameters.remove(Constants.GROUP_KEY);
        parameters.remove(Constants.VERSION_KEY);
        parameters.remove(Constants.APPLICATION_KEY);
        parameters.remove(Constants.CATEGORY_KEY);
        parameters.remove(Constants.DYNAMIC_KEY);
        parameters.remove(Constants.ENABLED_KEY);

        o.setEnabled(url.getParameter(Constants.ENABLED_KEY, true));

        String host = url.getHost();
        boolean anyhost = url.getParameter(Constants.ANYHOST_VALUE, false);
        if(!anyhost || !"0.0.0.0".equals(host)) {
            o.setAddress(url.getAddress());
        }

        o.setApplication(url.getParameter(Constants.APPLICATION_KEY, url.getUsername()));
        parameters.remove(Constants.VERSION_KEY);

        o.setParams(StringUtils.toQueryString(parameters));

        return o;
    }

}
