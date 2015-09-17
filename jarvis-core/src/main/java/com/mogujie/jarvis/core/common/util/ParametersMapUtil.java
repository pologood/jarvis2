/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月16日 下午2:51:33
 */

package com.mogujie.jarvis.core.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;

/**
 * @author guangming
 *
 */
public class ParametersMapUtil {
    public static String convert2String(Map<String, String> parameterMap) {
        String parameters = "";
        Iterator<Map.Entry<String, String>> it = parameterMap.entrySet().iterator();
        if (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            parameters = key + "=" + value;
        }
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            parameters = parameters + "," + key + "=" + value;
        }

        return parameters;
    }

    public static Map<String, Object> convert2Map(String parameters) {
        String[] entries = parameters.split(",");
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        for (String entry : entries) {
            int index = entry.indexOf("=");
            int length = entry.length();
            String key = entry.substring(0, index);
            String value = entry.substring(index + 1, length);
            parameterMap.put(key, value);
        }
        return parameterMap;
    }

    public static String convert2String(List<MapEntry> entryList) {
        String parameters = "";
        Iterator<MapEntry> it = entryList.iterator();
        if (it.hasNext()) {
            MapEntry entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            parameters = key + "=" + value;
        }
        while (it.hasNext()) {
            MapEntry entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            parameters = parameters + "," + key + "=" + value;
        }

        return parameters;
    }
}
