package com.dubboclub.dk.admin.service;

import com.alibaba.dubbo.common.URL;
import com.dubboclub.dk.admin.model.Override;
import com.dubboclub.dk.admin.model.Provider;

import java.util.List;

/**
 * Created by bieber on 2015/6/3.
 */
public interface OverrideService {

    public List<Override> listByProvider(Provider provider);

    public List<Override> listByServiceKey(String serviceKey);

    public void update(Override override);

    public Override getById(Long id);

    public void delete(Override override);

    public void delete(Long id);

    public void add(Override override);


    public Provider configProvider(Provider provider);

    public URL configProviderURL(Provider provider);
}
