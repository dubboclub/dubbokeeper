package com.dubboclub.web.controller;

import com.dubboclub.admin.service.OverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by bieber on 2015/6/20.
 */
@Controller
@RequestMapping("/override")
public class OverrideController {

    @Autowired
    private OverrideService overrideService;


    @RequestMapping("/provider/{serviceKey}/overrides.htm")
    public void listOverridesByProvider(@RequestParam("serviceKey")String serviceKey){


    }

}
