package com.dubboclub.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by bieber on 2015/6/4.
 */
@Controller
public class IndexController {

    @RequestMapping("/index.htm")
    public String index(){
        return "index";
    }
}
