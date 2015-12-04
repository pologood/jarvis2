package com.mogujie.jarvis.rest;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.mogujie.jarvis.rest.utils.JsonParameters;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.expression.ScheduleExpressionType;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.rest.vo.JobEntryVo;
import com.mogujie.jarvis.rest.vo.JobVo;
import com.mogujie.jarvis.server.domain.CommonStrategy;

/**
 * Created by muming on 15/12/1.
 */
public class TestRestJob {

    private String baseUrl = "http://127.0.0.1:8080";

    @Test
    public void jobSubmit() throws UnirestException {

        JobEntryVo job = new JobEntryVo();
        job.setJobName("mmTest");
        job.setJobType("hive");
        job.setJobFlag(1);
        job.setContent("show create table dw_site_app_clicklog;");
        job.setWorkerGroupId(1);

        // 计划表达式
        JobEntryVo.ScheduleExpressionEntry expressionEntry = new JobEntryVo.ScheduleExpressionEntry();
        expressionEntry.setExpressionType(ScheduleExpressionType.CRON.getValue());
        expressionEntry.setExpression("0 0 3 * * ?");
        job.setScheduleExpressionEntry(expressionEntry);

        // 依赖任务
        JobEntryVo.DependencyEntry dependencyEntry1 = new JobEntryVo.DependencyEntry();
        dependencyEntry1.setOperatorMode(OperationMode.ADD.getValue());
        dependencyEntry1.setPreJobId(1L);
        dependencyEntry1.setCommonStrategy(CommonStrategy.ALL.getValue());
        JobEntryVo.DependencyEntry dependencyEntry2 = new JobEntryVo.DependencyEntry();
        dependencyEntry2.setOperatorMode(OperationMode.ADD.getValue());
        dependencyEntry2.setPreJobId(2L);
        dependencyEntry2.setCommonStrategy(CommonStrategy.ALL.getValue());
        dependencyEntry2.setOffsetStrategy("cd");
        job.setDependencyList(Arrays.asList(dependencyEntry1, dependencyEntry2));


        //任务参数
        Map<String,Object> jobPrams = new HashMap<>();
        jobPrams.put("name","muming");
        jobPrams.put("age", 18);
        jobPrams.put("isMail", false);
        job.setParams(jobPrams);
//        job.setParams(Arrays.asList());

        String a = JsonParameters.toJson(jobPrams, Map.class);


        Map<String,Object> mapA = JsonParameters.fromJson2JobParams(a);



        // 任务参数
        String paramsJson = JsonHelper.toJson(job, JobEntryVo.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/job/submit").field("appName", "jarvis-web").field("appToken", "123")
                .field("user", "muming").field("parameters", paramsJson).asString();

        Type restType = new TypeToken<RestResult<JobVo>>() {
        }.getType();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        RestResult<?> result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(), 0);
    }

    @Test
    public void jobEdit() throws UnirestException {

        JobEntryVo job = new JobEntryVo();
        job.setJobId(13);
        job.setJobFlag(JobFlag.DELETED.getValue());
        String paramsJson = JsonHelper.toJson(job, JobEntryVo.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/job/edit").field("appName", "jarvis-web").field("appToken", "123")
                .field("user", "muming").field("parameters", paramsJson).asString();

        Type restType = new TypeToken<RestResult<JobVo>>() {
        }.getType();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        RestResult<?> result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(), 0);

    }

}