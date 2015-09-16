/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月16日 下午3:06:21
 */

package com.mogujie.jarvis.core.common.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author guangming
 *
 */
public class TestParameterMap {

    @Test
    public void testConvert2String() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("key1", "value1");
        parameterMap.put("key2", "value2");
        parameterMap.put("key3", "value3");
        String parameters = ParametersMapUtil.convert2String(parameterMap);
        Assert.assertEquals("key1=value1,key2=value2,key3=value3", parameters);
    }

    @Test
    public void testConvert2Map() {
        String parameters = "key1=value1,key2=value2,key3=value3";
        Map<String, Object> parameterMap = ParametersMapUtil.convert2Map(parameters);
        Assert.assertEquals(3, parameterMap.size());
        Assert.assertEquals("value1", parameterMap.get("key1"));
        Assert.assertEquals("value2", parameterMap.get("key2"));
        Assert.assertEquals("value3", parameterMap.get("key3"));
    }

}
