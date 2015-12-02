/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming
 * Create Date: 2015年10月08日 下午3:19:28
 */
package com.mogujie.jarvis.rest.controller;

import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.rest.utils.JsonParameters;
import com.mogujie.jarvis.rest.vo.JobEntryVo;
import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobResponse;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.vo.JobVo;

/**
 * @author muming
 */
@Path("api/job")
public class JobController extends AbstractController {

    /**
     * 提交job任务
     *
     * @throws Exception
     */
    @POST
    @Path("submit")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult submit(
            @FormParam("appName") String appName,
            @FormParam("appToken") String appToken,
            @FormParam("user") String user,
            @FormParam("parameters") String parameters) {

        LOGGER.debug("提交job任务");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JobEntryVo jobVo = JsonParameters.fromJson(parameters, JobEntryVo.class);
            //parameters处理
            String jobParameters = "";
            if(jobVo.getParams() != null) {
                jobParameters = JsonParameters.toJson(jobVo.getParams(), List.class);
            }
            // 构造新增任务请求
            RestSubmitJobRequest.Builder builder = RestSubmitJobRequest.newBuilder()
                    .setAppAuth(appAuth)
                    .setUser(user)
                    .setJobName(jobVo.getJobName())
                    .setJobType(jobVo.getJobType())
                    .setJobFlag(jobVo.getJobFlag())
                    .setContent(jobVo.getContent())
                    .setParameters(jobParameters)
                    .setAppName(jobVo.getAppName(appName))
                    .setWorkerGroupId(jobVo.getWorkerGroupId())
                    .setPriority(jobVo.getPriority(1))
                    .setActiveStartTime(jobVo.getActiveStartTime(0))
                    .setActiveEndTime(jobVo.getActiveEndTime(0))
                    .setRejectAttempts(jobVo.getRejectAttempts(0))
                    .setRejectInterval(jobVo.getRejectInterval(3))
                    .setFailedAttempts(jobVo.getFailedAttempts(0))
                    .setFailedInterval(jobVo.getFailedInterval(3));

            if (jobVo.getScheduleExpressionEntry() != null ) {
                ScheduleExpressionEntry entry = ScheduleExpressionEntry.newBuilder()
                        .setExpressionType(jobVo.getScheduleExpressionEntry().getExpressionType())
                        .setScheduleExpression(jobVo.getScheduleExpressionEntry().getExpression())
                        .build();
                builder.setExpressionEntry(entry);
            }
            if (jobVo.getDependencyList() != null && jobVo.getDependencyList().size() > 0) {
                for (JobEntryVo.DependencyEntry entryInput : jobVo.getDependencyList()) {
                    DependencyEntry entry = DependencyEntry.newBuilder()
                            .setJobId(entryInput.getPreJobId())
                            .setCommonDependStrategy(entryInput.getCommonStrategy())
                            .setOffsetDependStrategy(entryInput.getOffsetStrategy())
                            .build();
                    builder.addDependencyEntry(entry);
                }
            }

            // 发送请求到server尝试新增
            ServerSubmitJobResponse response = (ServerSubmitJobResponse) callActor(AkkaType.SERVER, builder.build());

            // 判断是否新增成功
            if (response.getSuccess()) {
                JobVo vo = new JobVo();
                vo.setJobId(response.getJobId());
                return successResult(vo);
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("", e);
            return errorResult(e.getMessage());
        }
    }


    /**
     * 修改job任务
     *
     * @throws Exception
     */
    @POST
    @Path("edit")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult edit(@FormParam("appName") String appName, @FormParam("appToken") String appToken, @FormParam("user") String user,
                           @FormParam("parameters") String parameters) {

        LOGGER.info("更新job任务");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JobEntryVo jobVo = JsonParameters.fromJson(parameters, JobEntryVo.class);

            //parameters处理
            String jobParameters = "";
            if(jobVo.getParams() != null) {
                jobParameters = JsonParameters.toJson(jobVo.getParams(), List.class);
            }

            // 构造修改job基本信息请求
            RestModifyJobRequest.Builder builder = RestModifyJobRequest.newBuilder()
                    .setAppAuth(appAuth)
                    .setUser(user)
                    .setJobId(jobVo.getJobId());

            if(jobVo.getJobName() != null  && !jobVo.getJobName().equals("")){
                builder.setJobName(jobVo.getJobName());
            }
            if(jobVo.getJobType() !=null){
                builder.setJobFlag(jobVo.getJobFlag());
            }
            if(jobVo.getContent() !=null && !jobVo.getContent().equals("")){
                builder.setContent(jobVo.getContent());
            }
            if(jobVo.getParams() !=null){
                builder.setParameters(jobParameters);
            }
            if(jobVo.getPriority() != null){
                builder.setPriority(jobVo.getPriority());
            }
            if(jobVo.getAppName() !=null){
                builder.setAppName(jobVo.getAppName());
            }
            if(jobVo.getWorkerGroupId() != null){
                builder.setWorkerGroupId(jobVo.getWorkerGroupId());
            }
            if(jobVo.getActiveStartTime() != null){
                builder.setActiveStartTime(jobVo.getActiveStartTime());
            }
            if(jobVo.getActiveEndTime() !=null){
                builder.setActiveEndTime(jobVo.getActiveEndTime());
            }
            if(jobVo.getRejectAttempts() != null){
                builder.setRejectAttempts(jobVo.getRejectAttempts());
            }
            if (jobVo.getRejectInterval() != null) {
                builder.setRejectInterval(jobVo.getRejectInterval());
            }
            if(jobVo.getFailedAttempts() != null){
                builder.setFailedAttempts(jobVo.getFailedAttempts());
            }
            if(jobVo.getFailedInterval() !=null){
                builder.setFailedInterval(jobVo.getFailedInterval());
            }
            if (jobVo.getScheduleExpressionEntry() != null ) {
                ScheduleExpressionEntry entry = ScheduleExpressionEntry.newBuilder()
                        .setExpressionType(jobVo.getScheduleExpressionEntry().getExpressionType())
                        .setScheduleExpression(jobVo.getScheduleExpressionEntry().getExpression())
                        .build();
                builder.setExpressionEntry(entry);
            }
            if (jobVo.getDependencyList() != null && jobVo.getDependencyList().size() > 0) {
                for (JobEntryVo.DependencyEntry entryInput : jobVo.getDependencyList()) {
                    DependencyEntry entry = DependencyEntry.newBuilder()
                            .setJobId(entryInput.getPreJobId())
                            .setCommonDependStrategy(entryInput.getCommonStrategy())
                            .setOffsetDependStrategy(entryInput.getOffsetStrategy())
                            .build();
                    builder.addDependencyEntry(entry);
                }
            }

            // 发送信息到server修改job基本信息
            ServerModifyJobResponse response = (ServerModifyJobResponse) callActor(AkkaType.SERVER, builder.build());

            // 判断修改基本信息是否成功，修改基本信息成功后才尝试修改依赖
            if (response.getSuccess()) {
                return successResult();
            }
            // 修改基本信息出错
            else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(e.getStackTrace());
            return errorResult(e.getMessage());
        }
    }


    /**
     * 重跑任务
     *
     * @throws Exception
     * @author hejian
     */

//    @POST
//    @Path("rerun")
//    @Produces(MediaType.APPLICATION_JSON)
//    public RestResult rerun(@FormParam("appName") String appName, @FormParam("appToken") String appToken, @FormParam("user") String user,
//                            @FormParam("parameters") String parameters) {
//        try {
//            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();
//
//            JSONObject para = new JSONObject(parameters);
//
//            String startTime = para.getString("startTime");
//            String endTime = para.getString("endTime");
//            String reRunJobs = para.getString("reRunJobs");
//
//            JSONArray reRunJobArr = new JSONArray(reRunJobs);
//
//            Long startTimeLong = null;
//            Long endTimeLong = null;
//            if (startTime != null && !startTime.equals("")) {
//                startTimeLong = dateTimeFormatter.parseDateTime(startTime).getMillis();
//            }
//            if (endTime != null && !endTime.equals("")) {
//                endTimeLong = dateTimeFormatter.parseDateTime(endTime).getMillis();
//            }
//
//            boolean flag = true;
//            JSONObject msg = new JSONObject();
//            for (int i = 0; i < reRunJobArr.length(); i++) {
//                Long singleOriginId = reRunJobArr.getLong(i);
//                // 构造新增任务请求
//                RestServerSubmitJobRequest.Builder builder = RestServerSubmitJobRequest.newBuilder().setAppAuth(appAuth).setUser(user);
//                if (startTimeLong != null) {
//                    builder.setStartTime(startTimeLong);
//                }
//                if (endTimeLong != null) {
//                    builder.setEndTime(endTimeLong);
//                }
//                RestServerSubmitJobRequest request = builder.build();
//                ServerSubmitJobResponse response = (ServerSubmitJobResponse) callActor(AkkaType.SERVER, request);
//
//                // 保存整理而言是否成功，如果某个job重跑失败，则算失败
//                flag = flag && response.getSuccess();
//                // 判断是否重跑成功
//                if (response.getSuccess()) {
//                    msg.put(singleOriginId.toString(), "success," + response.getMessage());
//                } else {
//                    msg.put(singleOriginId.toString(), "failed," + response.getMessage());
//                }
//            }
//
//            // 判断删除是否成功
//            if (flag) {
//                return new RestResult(MsgCode.SUCCESS);
//            } else {
//                return errorResult(msg.toString());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            LOGGER.info(e.getStackTrace());
//            return errorResult(e.getMessage());
//        }
//    }


    /**
     * 测试
     *
     * @throws Exception
     */
    @GET
    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult test() throws Exception {
        JobVo vo = new JobVo();
        vo.setJobId(123456);
        return successResult(vo);

    }

}