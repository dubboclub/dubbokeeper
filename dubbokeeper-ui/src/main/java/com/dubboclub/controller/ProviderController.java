package com.dubboclub.controller;

import com.alibaba.dubbo.common.Constants;
import com.dubboclub.admin.model.Provider;
import com.dubboclub.admin.service.ProviderService;
import com.dubboclub.model.BasicResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by bieber on 2015/6/7.
 */
@Controller
@RequestMapping("/provider")
public class ProviderController {

    @Autowired
    private ProviderService providerService;


    @RequestMapping("/{id}/providers.htm")
    public @ResponseBody List<Provider> listProviderByService(@PathVariable("id") Long id){
        Provider provider = providerService.getProviderById(id);
        return  providerService.listProviderByServiceKey(provider.getService());
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

        }else if("halfWeight".equals(type)){
            providerService.halfWeight(id);
        }else if("doubleWeight".equals(type)){
            providerService.doubleWeight(id);
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
