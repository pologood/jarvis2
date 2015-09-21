/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月17日 下午8:23:47
 */

package com.mogujie.jarvis.core.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.mogujie.jarvis.protocol.MapEntryProtos;
import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;

/**
 * @author guangming
 *
 */
public class TestJsonHelp {

    @Test
    public void testParseMap2JSON() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        String jsonStr = JsonHelper.parseMap2JSON(map);
        Assert.assertEquals("{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"value3\"}", jsonStr);
    }

    @Test
    public void testParseJSON2Map() {
        String jsonStr = "{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"value3\"}";
        Map<String, Object> jsonMap = JsonHelper.parseJSON2Map(jsonStr);
        Assert.assertEquals(3, jsonMap.size());
        Assert.assertEquals("value1", jsonMap.get("key1"));
        Assert.assertEquals("value2", jsonMap.get("key2"));
        Assert.assertEquals("value3", jsonMap.get("key3"));
    }

    @Test
    public void testParseList2JSON() {
        List<MapEntry> mapEntryList = new ArrayList<MapEntryProtos.MapEntry>();
        mapEntryList.add(MapEntry.newBuilder().setKey("key1").setValue("value1").build());
        mapEntryList.add(MapEntry.newBuilder().setKey("key2").setValue("value2").build());
        mapEntryList.add(MapEntry.newBuilder().setKey("key3").setValue("value3").build());
        String jsonStr = JsonHelper.parseMapEntryList2JSON(mapEntryList);
        Assert.assertEquals("{\"key1\":\"value1\",\"key2\":\"value2\",\"key3\":\"value3\"}", jsonStr);
    }
}
