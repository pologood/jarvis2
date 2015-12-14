package com.mogujie.jarvis.rest;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.rest.vo.JobVo;
import com.mogujie.jarvis.rest.vo.LogResultVo;
import com.mogujie.jarvis.rest.vo.TaskEntryVo;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by muming on 15/12/1.
 */
public class TestRestLog {

    private String baseUrl = "http://127.0.0.1:8080";

    public void testLogRead() throws UnirestException {

        Map<String, Object> params = new HashMap<>();
        params.put("fullId", "1001_1002_1");
        params.put("lines", 10);
        params.put("offset", 0);
        String paramsJson = JsonHelper.toJson(params, Map.class);

        while(true) {
            HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/log/readResult")
                    .field("appName", "jarvis-web")
                    .field("appToken", "123")
                    .field("user", "muming")
                    .field("parameters", paramsJson)
                    .asString();

            Assert.assertEquals(jsonResponse.getStatus(), 200);
            Type type = new TypeToken<TestRestResultEntity<LogResultVo>>(){}.getType();
            TestRestResultEntity<?> result = JsonHelper.fromJson(jsonResponse.getBody(), type);
            Assert.assertEquals(result.getCode(), 0);
            LogResultVo log = (LogResultVo)result.getData();
            System.out.print(log.getLog());
            if(log.isEnd()){
                break;
            }
            params.put("offset",log.getOffset());
            paramsJson = JsonHelper.toJson(params, Map.class);
        }
    }

}