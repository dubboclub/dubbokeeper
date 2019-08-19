package com.dubboclub.dk.admin.service.impl;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.RegistryConstants;

import com.dubboclub.dk.admin.model.Route;
import com.dubboclub.dk.admin.service.AbstractService;
import com.dubboclub.dk.admin.service.RouteService;
import com.dubboclub.dk.admin.sync.util.Pair;
import com.dubboclub.dk.admin.sync.util.SyncUtils;
import com.dubboclub.dk.admin.sync.util.Tool;

import java.util.List;

/**
 * Created by bieber on 2015/7/25.
 */
public class RouteServiceImpl extends AbstractService implements RouteService {

    @Override
    public void createRoute(Route route) {
        add(route.toUrl());
    }

    @Override
    public void deleteRoute(Long id) {
        delete(id, RegistryConstants.ROUTERS_CATEGORY);
    }

    @Override
    public void updateRoute(Route route) {
        URL oldUrl = getOneById(RegistryConstants.ROUTERS_CATEGORY,route.getId());
        update(oldUrl,route.toUrl());
    }

    @Override
    public List<Route> listByServiceKey(final String serviceKey) {
        return filterCategoryData(new ConvertURL2Entity<Route>() {

            @Override
            public Route convert(Pair<Long, URL> pair) {
                if(pair.getValue().getPath().equals(Tool.getInterface(serviceKey))){
                    return SyncUtils.url2Route(pair);
                }else{
                    return null;
                }
            }
        }, RegistryConstants.ROUTERS_CATEGORY, CommonConstants.VERSION_KEY, Tool.getVersion(serviceKey),CommonConstants.GROUP_KEY,Tool.getGroup(serviceKey));
    }

    @Override
    public Route getRoute(Long id) {
        return SyncUtils.url2Route(new Pair<Long, URL>(id,getOneById(RegistryConstants.ROUTERS_CATEGORY,id)));
    }

    @Override
    public void enable(Long id) {
        Route route = getRoute(id);
        if(route.isEnabled()){
            return ;
        }
        route.setEnabled(true);
        updateRoute(route);
    }

    @Override
    public void disable(Long id) {
        Route route = getRoute(id);
        if(!route.isEnabled()){
            return ;
        }
        route.setEnabled(false);
        updateRoute(route);
    }
}
