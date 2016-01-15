package com.mogujie.jarvis.rest;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.rest.vo.AbstractVo;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by muming on 15/12/1.
 */
public class TestRestAlarm extends TestRestAbstact {

    public void test() throws UnirestException {

        long jobId = 5;
        alarmAdd(jobId);
        alarmSet(jobId);
        alarmDelete(jobId);

    }


    private void alarmAdd(long jobId) throws UnirestException {

        Map<String, Object> params = new HashMap<>();
        params.put("jobId", jobId);
        params.put("alarmType", "1,2,3");
        params.put("receiver", "mingren,muming");
        params.put("status",1);
        String paramsJson = JsonHelper.toJson(params, Map.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/alarm/add")
                .field("appName", "jarvis-web")
                .field("appToken", "123")
                .field("user", "muming")
                .field("parameters", paramsJson).asString();

        Type restType = new TypeToken<TestRestResultEntity<AbstractVo>>() {
        }.getType();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        TestRestResultEntity<?> result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(), 0);
    }

    private void alarmSet(long jobId) throws UnirestException {

        Map<String, Object> params = new HashMap<>();
        params.put("jobId", jobId);
        params.put("alarmType", "1,2,3,4");
        params.put("receiver", "muming");
        params.put("status",2);
        String paramsJson = JsonHelper.toJson(params, Map.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/alarm/set")
                .field("appName", "jarvis-web")
                .field("appToken", "123")
                .field("user", "muming")
                .field("parameters", paramsJson).asString();

        Type restType = new TypeToken<TestRestResultEntity<AbstractVo>>() {
        }.getType();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        TestRestResultEntity<?> result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(), 0);
    }

    private void alarmDelete(long jobId) throws UnirestException {

        Map<String, Object> params = new HashMap<>();
        params.put("jobId", jobId);
        String paramsJson = JsonHelper.toJson(params, Map.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/alarm/delete")
                .field("appName", "jarvis-web")
                .field("appToken", "123")
                .field("user", "muming")
                .field("parameters", paramsJson).asString();

        Type restType = new TypeToken<TestRestResultEntity<AbstractVo>>() {
        }.getType();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        TestRestResultEntity<?> result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(), 0);
    }

}
