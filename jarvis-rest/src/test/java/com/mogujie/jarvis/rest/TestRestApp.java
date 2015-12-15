package com.mogujie.jarvis.rest;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.mogujie.jarvis.rest.vo.AbstractVo;
import com.mogujie.jarvis.rest.vo.JobRelationsVo;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.core.util.JsonHelper;

/**
 * Created by muming on 15/12/1.
 */
public class TestRestApp extends TestRestAbstact{

    public void appAdd() throws UnirestException {

        Map<String, Object> params = new HashMap<>();
        params.put("applicationName", "mumingTest2");
        params.put("status", 1);
        params.put("maxConcurrency", 20);
        String paramsJson = JsonHelper.toJson(params, Map.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/app/add").field("appName", "jarvis-web").field("appToken", "123")
                .field("user", "muming").field("parameters", paramsJson).asString();

        Type restType = new TypeToken<TestRestResultEntity<AbstractVo>>() {}.getType();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        TestRestResultEntity<?> result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(), 0);
    }

}
