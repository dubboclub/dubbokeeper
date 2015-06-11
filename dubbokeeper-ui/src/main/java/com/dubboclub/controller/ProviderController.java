package com.dubboclub.controller;

import com.dubboclub.admin.model.Provider;
import com.dubboclub.admin.service.ProviderService;
import com.dubboclub.model.BasicResponse;
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


    @RequestMapping("/{service}/providers.htm")
    public @ResponseBody List<Provider> listProviderByService(@PathVariable("service") String service){

        return  providerService.listProviderByService(service);
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



}
