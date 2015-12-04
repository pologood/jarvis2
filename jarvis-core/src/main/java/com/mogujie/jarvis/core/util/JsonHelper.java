/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月17日 下午8:01:45
 */

package com.mogujie.jarvis.core.util;

import java.lang.reflect.Type;
import java.util.Map;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * @author muming
 */
public class JsonHelper {

    static Type mapType = new TypeToken<Map<String, Object>>() {
    }.getType();
    private static Gson gson = new Gson();

    public static Map<String, Object> fromJson2JobParams(String json) throws JsonSyntaxException {
        if (json == null || json.equals(""))
            return null;
        return gson.fromJson(json, mapType);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return gson.fromJson(json, typeOfT);
    }

    public static String toJson(Object object, Type typeOfSrc) {
        return gson.toJson(object, typeOfSrc);
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

}
