package com.mogujie.jarvis.rest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.core.util.JsonHelper;

/**
 * Created by muming on 15/12/1.
 */
public class TestRestApp {

    private String baseUrl = "http://127.0.0.1:8080";

    @Test
    public void appAdd() throws UnirestException {

        Map<String, Object> params = new HashMap<>();
        params.put("applicationName", "mumingTest1");
        params.put("status", 1);
        params.put("maxConcurrency", 20);
        String paramsJson = JsonHelper.toJson(params, Map.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/app/add").field("appName", "jarvis-web").field("appToken", "123")
                .field("user", "muming").field("parameters", paramsJson).asString();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        RestResult<?> result = JsonHelper.fromJson(jsonResponse.getBody(), RestResult.class);
        Assert.assertEquals(result.getCode(), 0);
    }

}
