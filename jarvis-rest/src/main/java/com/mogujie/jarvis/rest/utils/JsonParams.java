package com.mogujie.jarvis.rest.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by muming on 15/11/26.
 */


public class JsonParams {

    Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
    private static Gson gson = new Gson();
    private Map<String, Object> data;

    public JsonParams(String jsonString) {
        try{
            data = gson.fromJson(jsonString, mapType);
        }catch (JsonSyntaxException ex){
        }
        if(data == null ){
            data = new HashMap<>();
        }
    }

    public Object getObject(String key, Object defaultVal) {
        if (data.containsKey(key)) {
            return data.get(key).toString();
        } else {
            return defaultVal;
        }
    }

    public Object getObject(String key) {
        return getObject(key,null);
    }

    public String getString(String key, String defaultVal) {
        if (data.containsKey(key)) {
            return data.get(key).toString();
        } else {
            return defaultVal;
        }
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getStringNotEmpty(String key) throws IllegalArgumentException{
        String value = getString(key, null);
        if(value == null || value.equals("")){
            throw new IllegalArgumentException( key + "不能为空");
        }
        return value;
    }

    public Integer getInteger(String key, Integer defaultVal) {
        if (data.containsKey(key)) {
            try {
                return Integer.parseInt(data.get(key).toString());
            } catch (NumberFormatException Ex) {
                return defaultVal;
            }
        } else {
            return defaultVal;
        }
    }

    public Integer getInteger(String key) {
        return getInteger(key,null);
    }

    public Integer getIntegerNotNull(String key) {
        Integer value = getInteger(key,null);
        if(value == null){
            throw new IllegalArgumentException( key + "不能为空");
        }
        return value;
    }

    public Long getLong(String key, Long defaultVal) {
        if (data.containsKey(key)) {
            try {
                return Long.parseLong(data.get(key).toString());
            } catch (NumberFormatException Ex) {
                return defaultVal;
            }
        } else {
            return defaultVal;
        }
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Long getLongNotNull(String key) {
        Long value = getLong(key, null);
        if(value == null){
            throw new IllegalArgumentException( key + "不能为空");
        }
        return value;
    }

}
