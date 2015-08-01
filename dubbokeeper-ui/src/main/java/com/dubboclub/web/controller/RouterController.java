package com.dubboclub.web.controller;

import com.alibaba.dubbo.common.Constants;
import com.dubboclub.admin.model.Provider;
import com.dubboclub.admin.model.Route;
import com.dubboclub.admin.service.ProviderService;
import com.dubboclub.admin.service.RouteService;
import com.dubboclub.web.model.BasicResponse;
import com.dubboclub.web.model.OverrideAbstractInfo;
import com.dubboclub.web.model.RouteAbstractInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bieber on 2015/7/25.
 */
@Controller
@RequestMapping("/route")
public class RouterController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private ProviderService providerService;

    @RequestMapping("provider/{serviceKey}/list.htm")
    public @ResponseBody List<Route> queryRoutesByServiceKey(@PathVariable("serviceKey")String serviceKey) throws UnsupportedEncodingException {
        serviceKey = URLDecoder.decode(serviceKey, "UTF-8");
        return routeService.listByServiceKey(serviceKey);
    }

    @RequestMapping("create.htm")
    public @ResponseBody
    BasicResponse createRoute(@RequestBody Route route){
        BasicResponse response = new BasicResponse();
        response.setResult(BasicResponse.SUCCESS);
        routeService.createRoute(route);
        return response;
    }

    @RequestMapping("batch-delete-{ids}.htm")
    public @ResponseBody BasicResponse batchDelete(@PathVariable("ids")String ids){
        BasicResponse response = new BasicResponse();
        String[] idArray = Constants.COMMA_SPLIT_PATTERN.split(ids);
        for(String id:idArray){
            routeService.deleteRoute(Long.parseLong(id));
        }
        response.setResult(BasicResponse.SUCCESS);
        return response;
    }


    @RequestMapping("{type}_{id}.htm")
    public @ResponseBody BasicResponse delete(@PathVariable("type")String type,@PathVariable("id")Long id){
        BasicResponse response = new BasicResponse();
        response.setResult(BasicResponse.SUCCESS);
        if("delete".equals(type)){
            routeService.deleteRoute(id);
        }else if("enable".equals(type)){
            routeService.enable(id);
        }else if("disable".equals(type)){
            routeService.disable(id);
        }else{
            response.setResult(BasicResponse.FAILED);
        }
        return response;
    }

    @RequestMapping("update.htm")
    public @ResponseBody BasicResponse updateRoute(@RequestBody Route route){
        BasicResponse response = new BasicResponse();
        response.setResult(BasicResponse.SUCCESS);
        routeService.updateRoute(route);
        return response;
    }


    @ResponseBody
    @RequestMapping("list.htm")
    public List<RouteAbstractInfo> list(){
        List<Provider> providers = providerService.listAllProvider();
        List<RouteAbstractInfo> routeAbstractInfos = new ArrayList<RouteAbstractInfo>();
        for(Provider provider :providers){
            RouteAbstractInfo routeAbstractInfo = new RouteAbstractInfo();
            routeAbstractInfo.setServiceKey(provider.getServiceKey());
            routeAbstractInfo.setApplicationName(provider.getApplication());
            routeAbstractInfo.setRouteCount(routeService.listByServiceKey(provider.getServiceKey()).size());
            routeAbstractInfos.add(routeAbstractInfo);
        }
        return routeAbstractInfos;
    }

    @RequestMapping("get_{id}.htm")
    public @ResponseBody Route getRoute(@PathVariable("id")Long id){
        return routeService.getRoute(id);
    }


}
