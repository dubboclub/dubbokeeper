package com.dubboclub.web.controller;

import com.dubboclub.admin.model.Route;
import com.dubboclub.admin.service.RouteService;
import com.dubboclub.web.model.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by bieber on 2015/7/25.
 */
@Controller
@RequestMapping("/route")
public class RouterController {

    @Autowired
    private RouteService routeService;


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

    @RequestMapping("get_{id}.htm")
    public @ResponseBody Route getRoute(@PathVariable("id")Long id){
        return routeService.getRoute(id);
    }


}
