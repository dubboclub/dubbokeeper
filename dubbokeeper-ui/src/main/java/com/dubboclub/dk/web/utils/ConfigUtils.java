package com.dubboclub.dk.web.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Properties;

/**
 * Created by bieber on 2015/6/1.
 */
public class ConfigUtils {

    private static final Properties PROPERTIES = new Properties();


    public static void appendProperties(Properties properties){
        if(properties==null){
            throw new IllegalArgumentException("properties must not null");
        }
        PROPERTIES.putAll(properties);
    }

    public static String getProperty(String key){
        Object value= PROPERTIES.get(key);
        return value==null?null:value.toString();
    }

    public static int getProperty(String key,int defaultValue){
        String value =  getProperty(key);
        if(StringUtils.isEmpty(value)){
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    public static short getProperty(String key,short defaultValue){
        String value =  getProperty(key);
        if(StringUtils.isEmpty(value)){
            return defaultValue;
        }
        return Short.parseShort(value);
    }
    public static long getProperty(String key,long defaultValue){
        String value =  getProperty(key);
        if(StringUtils.isEmpty(value)){
            return defaultValue;
        }
        return Long.parseLong(value);
    }

    public static String getProperty(String key,String defaultValue){
        String value =  getProperty(key);
        if(StringUtils.isEmpty(value)){
            return defaultValue;
        }
        return value;
    }
    public static float getProperty(String key,float defaultValue){
        String value =  getProperty(key);
        if(StringUtils.isEmpty(value)){
            return defaultValue;
        }
        return Float.parseFloat(value);
    }
    public static double getProperty(String key,double defaultValue){
        String value =  getProperty(key);
        if(StringUtils.isEmpty(value)){
            return defaultValue;
        }
        return Double.parseDouble(value);
    }
}
