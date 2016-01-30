package com.mogujie.jarvis.rest;

import java.lang.reflect.Type;
import java.util.*;

import com.mogujie.jarvis.rest.domain.RestResult4TestEntity;
import com.mogujie.jarvis.rest.vo.*;
import org.junit.Assert;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.expression.ScheduleExpressionType;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.core.domain.CommonStrategy;
import org.junit.Test;

/**
 * Created by muming on 15/12/1.
 */
public class TestJobRest {

    @Test
    public void testMy(){
        String a = "{\"jobName\":\"test\",\"activeStartDate\":\"\",\"activeEndDate\":\"\",\"content\":\"ls;\",\"params\":\"{}\",\"expression\":\"\",\"failedAttempts\":0,\"failedInterval\":3,\"jobType\":\"hive_script\",\"bizGroupId\":1,\"workerGroupId\":1,\"expressionType\":1,\"priority\":1,\"scheduleExpressionList\":[]}";
        JobVo jobVo = JsonHelper.fromJson(a, JobVo.class);
        String b = "{\"jobName\":\"test\",\"activeStartDate\":\"\",\"activeEndDate\":\"\",\"content\":\"ls;\",\"params\":\"{}\",\"expression\":\"\",\"failedAttempts\":0,\"failedInterval\":3,\"jobType\":\"hive_script\",\"bizGroupId\":1,\"workerGroupId\":1,\"expressionType\":1,\"priority\":1,\"scheduleExpressionList\":[{\"expressionType\":\"1\",\"expression\":\"\",\"operatorMode\":3}]}";
        JobVo jobVo2= JsonHelper.fromJson(b, JobVo.class);

        int i = 3;
    }


    private String baseUrl = "http://127.0.0.1:8080";
//    private String baseUrl = "http://10.11.129.54:8080";

    public void testJobSubmit() throws UnirestException {
        Long jobId = jobSubmit();

    }

    public void testJobScheduleSet() throws UnirestException{
//        Long jobId = jobSubmit();
        Long jobId = 299L;
        jobScheduleExpSet(jobId);
    }

    public void testJobDependencySet() throws UnirestException{
        Long jobId = jobSubmit();
        jobDependencySet(jobId);
    }

    private Long jobSubmit() throws UnirestException {

        JobVo job = new JobVo();
        job.setJobName("mmTest");
        job.setJobType("hive");
        job.setStatus(1);
        job.setContent("show create table dw_site_app_clicklog;");
        job.setWorkerGroupId(1);

        // 计划表达式
        List<JobScheduleExpVo.ScheduleExpressionEntry> list = new ArrayList<>();
        JobScheduleExpVo.ScheduleExpressionEntry expressionEntry;

        expressionEntry = new JobScheduleExpVo.ScheduleExpressionEntry();
        expressionEntry.setOperatorMode(OperationMode.ADD.getValue());
        expressionEntry.setExpressionType(ScheduleExpressionType.CRON.getValue());
        expressionEntry.setExpression("0 0 3 * * ?");
        list.add(expressionEntry);

        expressionEntry = new JobScheduleExpVo.ScheduleExpressionEntry();
        expressionEntry.setOperatorMode(OperationMode.ADD.getValue());
        expressionEntry.setExpressionType(ScheduleExpressionType.CRON.getValue());
        expressionEntry.setExpression("0 0 4 * * ?");
        list.add(expressionEntry);

        job.setScheduleExpressionList(list);


        // 依赖任务
        List<JobDependencyVo.DependencyEntry> dependList = new ArrayList<>();
        JobDependencyVo.DependencyEntry dependencyEntry;

        dependencyEntry = new JobDependencyVo.DependencyEntry();
        dependencyEntry.setOperatorMode(OperationMode.ADD.getValue());
        dependencyEntry.setPreJobId(1L);
        dependencyEntry.setCommonStrategy(CommonStrategy.ALL.getValue());
        dependList.add(dependencyEntry);

        dependencyEntry = new JobDependencyVo.DependencyEntry();
        dependencyEntry.setOperatorMode(OperationMode.ADD.getValue());
        dependencyEntry.setPreJobId(2L);
        dependencyEntry.setCommonStrategy(CommonStrategy.ALL.getValue());
        dependencyEntry.setOffsetStrategy("cd");
        dependList.add(dependencyEntry);

        job.setDependencyList(dependList);

        //任务参数
        Map<String,Object> jobPrams = new HashMap<>();
        jobPrams.put("name","muming");
        jobPrams.put("age", 18);
        jobPrams.put("isMail", false);
        jobPrams.put("params","{\"para1\":\"1\",\"para2\":\"2\"}");
        job.setParams(JsonHelper.toJson(jobPrams));

        // 任务参数
        String paramsJson = JsonHelper.toJson(job, JobVo.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/job/submit").field("appName", "jarvis-web").field("appToken", "123")
                .field("user", "muming").field("parameters", paramsJson).asString();

        Type restType = new TypeToken<RestResult4TestEntity<JobResultVo>>() {}.getType();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        RestResult4TestEntity<JobResultVo> result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(), 0);

        return result.getData().getJobId();

    }

    private void jobScheduleExpSet(long jobId) throws UnirestException {

        JobScheduleExpVo job = new JobScheduleExpVo();
        job.setJobId(jobId);

        List<JobScheduleExpVo.ScheduleExpressionEntry> list = new ArrayList<>();
        JobScheduleExpVo.ScheduleExpressionEntry expressionEntry;


        expressionEntry = new JobScheduleExpVo.ScheduleExpressionEntry();
        expressionEntry.setOperatorMode(OperationMode.DELETE.getValue());
        expressionEntry.setExpressionId(33L);
        list.add(expressionEntry);

        expressionEntry = new JobScheduleExpVo.ScheduleExpressionEntry();
        expressionEntry.setOperatorMode(OperationMode.EDIT.getValue());
        expressionEntry.setExpressionId(34L);
        expressionEntry.setExpression("0 43 5 * * ?");
        expressionEntry.setExpressionType(ScheduleExpressionType.CRON.getValue());
        list.add(expressionEntry);

        expressionEntry = new JobScheduleExpVo.ScheduleExpressionEntry();
        expressionEntry.setOperatorMode(OperationMode.ADD.getValue());
        expressionEntry.setExpression("0 0 6 * * ?");
        expressionEntry.setExpressionType(ScheduleExpressionType.CRON.getValue());
        list.add(expressionEntry);

        job.setScheduleExpressionList(list);

        String paramsJson = JsonHelper.toJson(job, JobScheduleExpVo.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/job/scheduleExp/set")
                .field("appName", "jarvis-web")
                .field("appToken", "123")
                .field("user", "muming")
                .field("parameters", paramsJson).asString();

        Type restType = new TypeToken<RestResult4TestEntity<JobResultVo>>() {
        }.getType();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        RestResult4TestEntity<?> result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(), 0);

    }

    private void jobDependencySet(long jobId) throws UnirestException {

        JobDependencyVo job = new JobDependencyVo();
        job.setJobId(jobId);

        List<JobDependencyVo.DependencyEntry> list = new ArrayList<>();
        JobDependencyVo.DependencyEntry entry;

        // 依赖任务
        entry = new JobDependencyVo.DependencyEntry();
        entry.setOperatorMode(OperationMode.ADD.getValue());
        entry.setPreJobId(3L);
        entry.setCommonStrategy(CommonStrategy.ALL.getValue());
        entry.setOffsetStrategy("cd");
        list.add(entry);

        entry = new JobDependencyVo.DependencyEntry();
        entry.setOperatorMode(OperationMode.EDIT.getValue());
        entry.setPreJobId(2L);
        entry.setCommonStrategy(CommonStrategy.ANYONE.getValue());
        entry.setOffsetStrategy("cw");
        list.add(entry);

        entry = new JobDependencyVo.DependencyEntry();
        entry.setOperatorMode(OperationMode.DELETE.getValue());
        entry.setPreJobId(1L);
        list.add(entry);


        job.setDependencyList(list);

        String paramsJson = JsonHelper.toJson(job, JobDependencyVo.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/job/dependency/set")
                .field("appName", "jarvis-web")
                .field("appToken", "123")
                .field("user", "muming")
                .field("parameters", paramsJson).asString();

        Type restType = new TypeToken<RestResult4TestEntity<JobResultVo>>() {
        }.getType();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        RestResult4TestEntity<?> result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(), 0);

    }

    public void queryRelations() throws UnirestException {

        Map<String, Object> params = new HashMap<>();
        params.put("jobId", 2);
        params.put("relationType", 1);
        String paramsJson = JsonHelper.toJson(params, Map.class);

        HttpResponse<String> jsonResponse = Unirest.post(baseUrl + "/api/job/queryRelation")
                .field("appName", "jarvis-web")
                .field("appToken", "123")
                .field("user", "muming")
                .field("parameters", paramsJson).asString();

        Type restType = new TypeToken<RestResult4TestEntity<JobRelationsVo>>() {}.getType();

        Assert.assertEquals(jsonResponse.getStatus(), 200);
        RestResult4TestEntity<?> result = JsonHelper.fromJson(jsonResponse.getBody(), restType);
        Assert.assertEquals(result.getCode(), 0);

    }


}