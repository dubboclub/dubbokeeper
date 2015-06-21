package com.dubboclub.web.controller;

import com.dubboclub.admin.model.*;
import com.dubboclub.admin.model.Override;
import com.dubboclub.admin.service.OverrideService;
import com.dubboclub.web.model.LoadBalanceOverrideInfo;
import com.dubboclub.web.model.OverrideInfo;
import com.dubboclub.web.model.WeightOverrideInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bieber on 2015/6/20.
 */
@Controller
@RequestMapping("/override")
public class OverrideController {

    @Autowired
    private OverrideService overrideService;


    @RequestMapping("/provider/{serviceKey}/list.htm")
    public @ResponseBody List<OverrideInfo>  listOverridesByProvider(@PathVariable("serviceKey")String serviceKey) throws UnsupportedEncodingException {
       List<Override> overrideList =  overrideService.listByServiceKey(URLDecoder.decode(serviceKey,"UTF-8"));
        List<OverrideInfo> overrideInfos = new ArrayList<OverrideInfo>();
        for(Override override:overrideList){
            overrideInfos.add(OverrideInfo.valueOf(override));
        }
        return overrideInfos;
    }


    @RequestMapping("/provider/{serviceKey}/weight-list.htm")
    public @ResponseBody List<WeightOverrideInfo>  listWeightOverridesByProvider(@PathVariable("serviceKey")String serviceKey) throws UnsupportedEncodingException {
        List<Override> overrideList =  overrideService.listByServiceKey(URLDecoder.decode(serviceKey,"UTF-8"));
        List<WeightOverrideInfo> overrideInfos = new ArrayList<WeightOverrideInfo>();
        for(Override override:overrideList){
            WeightOverrideInfo weightOverrideInfo = WeightOverrideInfo.valueOf(override);
            if(weightOverrideInfo!=null){
                overrideInfos.add(weightOverrideInfo);
            }
        }
        return overrideInfos;
    }

    @RequestMapping("/provider/{serviceKey}/loadbalance-list.htm")
    public @ResponseBody List<LoadBalanceOverrideInfo>  listLoadBalanceOverridesByProvider(@PathVariable("serviceKey")String serviceKey) throws UnsupportedEncodingException {
        List<Override> overrideList =  overrideService.listByServiceKey(URLDecoder.decode(serviceKey,"UTF-8"));
        List<LoadBalanceOverrideInfo> overrideInfos = new ArrayList<LoadBalanceOverrideInfo>();
        for(Override override:overrideList){
            LoadBalanceOverrideInfo loadBalanceOverrideInfo = LoadBalanceOverrideInfo.valueOf(override);
            if(loadBalanceOverrideInfo!=null){
                overrideInfos.add(loadBalanceOverrideInfo);
            }
        }
        return overrideInfos;
    }
}
