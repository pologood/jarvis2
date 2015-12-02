package com.mogujie.jarvis.rest;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.rest.vo.JobEntryVo;
import com.mogujie.jarvis.rest.vo.JobVo;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by muming on 15/12/1.
 */
public class JobRestTest {

    private String baseUrl ="http://127.0.0.1:8080";


    @Test
    public void jobSubmit() throws UnirestException {

        Map<Integer,String> a =  new HashMap<>();


        JobEntryVo job = new JobEntryVo();
        job.setJobName("mmTest");
        job.setJobType("hive");
        job.setJobFlag(1);
        job.setContent("show create table dw_site_app_clicklog;");
        job.setWorkerGroupId(1);
        String paramsJson = JsonHelper.toJson(job,JobEntryVo.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/job/submit")
                .field("appName", "jarvis-web")
                .field("appToken", "123")
                .field("user", "muming")
                .field("parameters", paramsJson)
                .asString();

        Type restType = new TypeToken<RestResult<JobVo>>() {}.getType();


        Assert.assertEquals(jsonResponse.getStatus(), 200);
        RestResult result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(),0);
    }





}
