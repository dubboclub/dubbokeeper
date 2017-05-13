package com.dubboclub.dk.admin.service;

import com.dubboclub.dk.admin.model.Route;

import java.util.List;

/**
 * Created by bieber on 2015/6/3.
 */
public interface RouteService {

    public void createRoute(Route route);

    public void deleteRoute(Long id);

    public void updateRoute(Route route);

    public List<Route> listByServiceKey(String serviceKey);

    public Route getRoute(Long id);

    public void enable(Long id);

    public void disable(Long id);


}
