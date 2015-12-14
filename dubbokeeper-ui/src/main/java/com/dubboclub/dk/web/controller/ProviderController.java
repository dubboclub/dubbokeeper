package com.dubboclub.dk.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import com.dubboclub.dk.admin.service.OverrideService;
import com.dubboclub.dk.web.model.BasicResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.dubboclub.dk.admin.model.Provider;
import com.dubboclub.dk.admin.service.ProviderService;
import com.dubboclub.dk.admin.sync.util.Tool;

/**
 * Created by bieber on 2015/6/7.
 */
@Controller
@RequestMapping("/provider")
public class ProviderController {

    @Autowired
    private ProviderService providerService;
    @Autowired
    private OverrideService overrideService;


    @RequestMapping("/{serviceKey}/providers.htm")
    public @ResponseBody List<Provider> listProviderByService(@PathVariable("serviceKey") String serviceKey) throws UnsupportedEncodingException {
        return  providerService.listProviderByServiceKey(URLDecoder.decode(serviceKey,"utf-8"));
    }
    @RequestMapping("/{applicationName}/{host}/providers.htm")
    public @ResponseBody List<Provider> listProviderByHost(@PathVariable("applicationName")String applicationName,@PathVariable("host") String host){
        List<Provider> providers = providerService.listProviderByApplication(applicationName);
        Iterator<Provider> iterator = providers.iterator();
        List<Provider> providerList = new ArrayList<Provider>();
        while(iterator.hasNext()){
            Provider provider = iterator.next();
            if(!provider.getAddress().equals(host)){
                iterator.remove();
            }else{
                providerList.add(overrideService.configProvider(provider));
            }
        }
        return providerList;
    }
    
    @RequestMapping("/{serviceKey}/service-readme.htm")
    public @ResponseBody Map<String,Object> seriveReadMe(@PathVariable("serviceKey") String serviceKey) throws UnsupportedEncodingException {
    	Map<String,Object> re = new HashMap<String, Object>();
    	re.put("providers", providerService.listProviderByServiceKey(URLDecoder.decode(serviceKey,"UTF-8")));
    	re.put("registry", ConfigUtils.getProperty("dubbo.registry.address"));
    	return re;
    }


    @RequestMapping("/{id}/provider-detail.htm")
    public @ResponseBody Provider loadProviderDetail(@PathVariable("id")long id){
        return providerService.getProviderById(id);
    }


    @RequestMapping(value = "/edit-provider.htm",method = RequestMethod.POST)
    public @ResponseBody
    BasicResponse editProvider(@RequestParam("parameters")String parameters,@RequestParam("id")long id){
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setResult(BasicResponse.SUCCESS);
        Provider provider =providerService.getProviderById(id);
        provider.setParameters(parameters);
        Map<String,String> params = Tool.convertParametersMap(provider.getParameters());
        provider.setEnabled(Boolean.parseBoolean(params.get(Constants.ENABLED_KEY)));
        provider.setWeight(Integer.parseInt(params.get(Constants.WEIGHT_KEY)));
        providerService.updateProvider(provider);
        return basicResponse;
    }

    @RequestMapping(value = "/{id}/{type}/operate.htm",method = RequestMethod.POST)
    public @ResponseBody BasicResponse operate(@PathVariable("id") long id,@PathVariable("type")String type){
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setResult(BasicResponse.SUCCESS);
        if("disable".equals(type)){
            providerService.disable(id);
        }else if("enable".equals(type)){
            providerService.enable(id);
        }else if("delete".equals(type)){
            providerService.delete(id);
        }else if("halfWeight".equals(type)){
            providerService.halfWeight(id);
        }else if("doubleWeight".equals(type)){
            providerService.doubleWeight(id);
        }else if("copy".equals(type)){
            providerService.copy(id);
        }
        return basicResponse;
    }

    @RequestMapping(value = "/{type}/batch-operate.htm",method = RequestMethod.POST)
    public @ResponseBody BasicResponse batchOperate(@PathVariable("type")String type,@RequestParam("ids") String ids){
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setResult(BasicResponse.SUCCESS);
        String[] idArray = StringUtils.split(ids,",");

        if("disable".equals(type)){
            for(String id:idArray){
                providerService.disable(Long.parseLong(id));
            }
        }else if("enable".equals(type)){
            for(String id:idArray){
                providerService.enable(Long.parseLong(id));
            }
        }else if("delete".equals(type)){
            for(String id:idArray){
               providerService.delete(Long.parseLong(id));
            }
        }else if("halfWeight".equals(type)){
            for(String id:idArray){
                providerService.halfWeight(Long.parseLong(id));
            }
        }else if("doubleWeight".equals(type)){
            for(String id:idArray){
                providerService.doubleWeight(Long.parseLong(id));
            }
        }
        return basicResponse;
    }


}
