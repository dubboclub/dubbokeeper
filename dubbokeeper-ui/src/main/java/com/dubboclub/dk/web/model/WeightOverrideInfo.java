package com.dubboclub.dk.web.model;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.cluster.Constants;

import java.util.Map;

/**
 * Created by bieber on 2015/6/21.
 */
public class WeightOverrideInfo extends  OverrideInfo{

    private int weight;

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    public static WeightOverrideInfo valueOf(com.dubboclub.dk.admin.model.Override override){
        String weight = null;
        if(!org.apache.commons.lang3.StringUtils.isEmpty(override.getParams())){
            Map<String,String> parameters = StringUtils.parseQueryString(override.getParams());
            weight=parameters.get(Constants.WEIGHT_KEY);
            if(org.apache.commons.lang3.StringUtils.isEmpty(weight)||"null".equals(weight)){
                return null;
            }
        }
        WeightOverrideInfo overrideInfo = new WeightOverrideInfo();
        overrideInfo.setAddress(override.getAddress());
        overrideInfo.setApplication(override.getApplication()==null?CommonConstants.ANY_VALUE:override.getApplication());
        overrideInfo.setEnable(override.isEnabled());
        overrideInfo.setId(override.getId());
        overrideInfo.setParameters(override.getParams());
        if(!org.apache.commons.lang3.StringUtils.isEmpty(weight)){
            overrideInfo.setWeight(Integer.parseInt(weight));
        }
        return overrideInfo;
    }
}
