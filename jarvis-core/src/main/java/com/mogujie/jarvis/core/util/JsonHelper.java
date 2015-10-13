/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月17日 下午8:01:45
 */

package com.mogujie.jarvis.core.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;

/**
 * @author guangming
 *
 */
public class JsonHelper {
    public static String parseMap2JSON(Map<String, String> map) {
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

    public static Map<String, Object> parseJSON2Map(String jsonStr) {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        JSONObject jsonObject = new JSONObject(jsonStr);
        @SuppressWarnings("unchecked")
        Iterator<String> it = jsonObject.keys();
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            Object value = jsonObject.get(key);
            jsonMap.put(key, value);
        }
        return jsonMap;
    }

    public static String parseMapEntryList2JSON(List<MapEntry> mapEntryList) {
        Map<String, String> jsonMap = new HashMap<String, String>();
        for (MapEntry mapEntry : mapEntryList) {
            jsonMap.put(mapEntry.getKey(), mapEntry.getValue());
        }
        JSONObject jsonObject = new JSONObject(jsonMap);
        return jsonObject.toString();
    }
}
