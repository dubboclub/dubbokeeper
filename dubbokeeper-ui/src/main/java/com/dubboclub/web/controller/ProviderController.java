package com.dubboclub.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.dubboclub.admin.model.Provider;
import com.dubboclub.admin.service.ProviderService;
import com.dubboclub.admin.sync.util.Tool;
import com.dubboclub.web.model.BasicResponse;
import com.dubboclub.web.properties.DubboKeeperConfigurer;

/**
 * Created by bieber on 2015/6/7.
 */
@Controller
@RequestMapping("/provider")
public class ProviderController {

    @Autowired
    private ProviderService providerService;


    @RequestMapping("/{service}/providers.htm")
    public @ResponseBody List<Provider> listProviderByService(@RequestParam("serviceKey") String serviceKey) throws UnsupportedEncodingException {
        return  providerService.listProviderByServiceKey(serviceKey);
    }
    
    @RequestMapping("/{service}/service-readme.htm")
    public @ResponseBody Map<String,Object> seriveReadMe(@RequestParam("serviceKey") String serviceKey) throws UnsupportedEncodingException {
    	Map<String,Object> re = new HashMap<String, Object>();
    	re.put("providers", providerService.listProviderByServiceKey(serviceKey));
    	re.put("registry", ConfigUtils.getProperty("dubbo.registry.address"));
    	return re;
    }


    @RequestMapping("/{id}/provider-detail.htm")
    public @ResponseBody Provider loadProviderDetail(@PathVariable("id")long id){
        return providerService.getProviderById(id);
    }


    @RequestMapping(value = "/edit-provider.htm",method = RequestMethod.POST)
    public @ResponseBody BasicResponse editProvider(@RequestParam("parameters")String parameters,@RequestParam("id")long id){
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
