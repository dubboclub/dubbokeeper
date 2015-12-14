package com.dubboclub.dk.web.properties;

import com.dubboclub.dk.web.utils.ConfigUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by bieber on 2015/6/1.
 */
public class DubboKeeperConfigurer extends PropertyPlaceholderConfigurer {

    @Override
    protected Properties mergeProperties() throws IOException {
        Properties properties =  super.mergeProperties();
        ConfigUtils.appendProperties(properties);
        return properties;
    }
}
