package com.dubboclub.controller;

import com.dubboclub.admin.model.Provider;
import com.dubboclub.admin.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

}
