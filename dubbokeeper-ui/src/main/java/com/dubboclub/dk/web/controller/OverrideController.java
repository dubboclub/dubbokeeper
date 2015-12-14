package com.dubboclub.dk.web.controller;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.dubboclub.dk.admin.model.Override;
import com.dubboclub.dk.admin.model.Provider;
import com.dubboclub.dk.admin.service.OverrideService;
import com.dubboclub.dk.admin.service.ProviderService;
import com.dubboclub.dk.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bieber on 2015/6/20.
 */
@Controller
@RequestMapping("/override")
public class OverrideController {

    @Autowired
    private OverrideService overrideService;

    @Autowired
    private ProviderService providerService;



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

    @RequestMapping("/provider/{serviceKey}/methods.htm")
    public @ResponseBody List<String> loadMethodsByServiceKey(@PathVariable("serviceKey")String serviceKey) throws UnsupportedEncodingException {
        List<Provider> providers = providerService.listProviderByServiceKey(URLDecoder.decode(serviceKey, "UTF-8"));
        List<String> methods = new ArrayList<String>();
        if(providers.size()>0){
            Provider provider = providers.get(0);
            Map<String,String> params = StringUtils.parseQueryString(provider.getParameters());
            String methodStr = params.get(Constants.METHODS_KEY);
            if(!StringUtils.isEmpty(methodStr)){
                String[] methodArray = Constants.COMMA_SPLIT_PATTERN.split(methodStr);
                for(String method:methodArray){
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    @RequestMapping("/provider/{serviceKey}/saveOverride.htm")
    public @ResponseBody
    BasicResponse saveOverride(@PathVariable("serviceKey")String serviceKey,@RequestBody OverrideInfo overrideInfo) throws UnsupportedEncodingException {
        Override override = overrideInfo.toOverride();
        override.setService(URLDecoder.decode(serviceKey, "UTF-8"));
        BasicResponse response = new BasicResponse();
        if(overrideInfo.getId()==null){
            overrideService.add(override);
        }else{
            override.setId(overrideInfo.getId());
            overrideService.update(override);
        }
        return response;
    }

    @RequestMapping("/{id}/{type}.htm")
    public @ResponseBody BasicResponse operate(@PathVariable("id")Long id,@PathVariable("type")String type){
        BasicResponse response = new BasicResponse();
        if("enable".equals(type)){
            Override override = overrideService.getById(id);
            override.setEnabled(true);
            overrideService.update(override);
        }else if("disable".equals(type)){
            Override override = overrideService.getById(id);
            override.setEnabled(false);
            overrideService.update(override);
        }else if("delete".equals(type)){
            overrideService.delete(id);
        }else{
            response.setMemo("未知操作！");
            response.setResult(BasicResponse.FAILED);
        }

        return response;
    }

    @RequestMapping("/batch/{type}.htm")
    public @ResponseBody BasicResponse batchOperate(@RequestParam("ids")String ids,@PathVariable("type")String type){
        BasicResponse response = new BasicResponse();
        String[] idArray = Constants.COMMA_SPLIT_PATTERN.split(ids);
        if("enable".equals(type)){
            for(String id:idArray){
                Override override = overrideService.getById(Long.parseLong(id));
                if(override.isEnabled()){
                    continue;
                }
                override.setEnabled(true);
                overrideService.update(override);
            }
        }else if("disable".equals(type)){
            for(String id:idArray){
                Override override = overrideService.getById(Long.parseLong(id));
                if(!override.isEnabled()){
                    continue;
                }
                override.setEnabled(false);
                overrideService.update(override);
            }
        }else if("delete".equals(type)){
            for(String id :idArray){
                overrideService.delete(Long.parseLong(id));
            }
        }else{
            response.setMemo("未知操作！");
            response.setResult(BasicResponse.FAILED);
        }

        return response;
    }


    @RequestMapping("/{id}/detail.htm")
    public @ResponseBody OverrideInfo getOverrideById(@PathVariable("id")Long id){
        Override override =  overrideService.getById(id);
        return OverrideInfo.valueOf(override);
    }


    @RequestMapping("/list.htm")
    public @ResponseBody List<OverrideAbstractInfo> listOverrideInfos(){
        List<Provider> providers = providerService.listAllProvider();
        List<OverrideAbstractInfo> overrideAbstractInfos = new ArrayList<OverrideAbstractInfo>();
        for(Provider provider :providers){
            OverrideAbstractInfo overrideAbstractInfo = new OverrideAbstractInfo();
            overrideAbstractInfo.setServiceKey(provider.getServiceKey());
            overrideAbstractInfo.setApplicationName(provider.getApplication());
            overrideAbstractInfo.setOverrideCount( overrideService.listByServiceKey(overrideAbstractInfo.getServiceKey()).size());
            if(overrideAbstractInfo.getOverrideCount()>0&&!overrideAbstractInfos.contains(overrideAbstractInfo)){
                overrideAbstractInfos.add(overrideAbstractInfo);
            }
        }
        return overrideAbstractInfos;
    }


}
