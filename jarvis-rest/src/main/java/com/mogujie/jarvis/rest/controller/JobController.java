/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming
 * Create Date: 2015年10月08日 下午3:19:28
 */
package com.mogujie.jarvis.rest.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.MapEntryProtos;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos.RestServerModifyDependencyRequest;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos.ServerModifyDependencyResponse;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.RestServerModifyJobFlagRequest;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.ServerModifyJobFlagResponse;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.ModifyJobProtos.ServerModifyJobResponse;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.rest.MsgCode;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.vo.JobVo;

/**
 * @author muming
 */
@Path("job")
public class JobController extends AbstractController {
    private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    Logger logger = Logger.getLogger(this.getClass());

    /**
     * 提交job任务
     *
     * @throws Exception
     */
    @POST
    @Path("submitJob")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult submitJob(@FormParam("appName") String appName, @FormParam("appKey") String appKey, @FormParam("jobName") String jobName,
            @FormParam("jobId") Long jobId, @FormParam("cronExpression") String cronExp, @FormParam("dependJobIds") String dependJobIds,
            @FormParam("user") String user, @FormParam("jobType") String jobType, @FormParam("content") String content,
            // @FormParam("jobContent") String jobContent,
            @FormParam("groupId") int groupId, @FormParam("rejectRetries") int rejectRetries, @FormParam("rejectInterval") int rejectInterval,
            @FormParam("failedRetries") int failedRetries, @FormParam("failedInterval") int failedInterval, @FormParam("startTime") String startTime,
            @FormParam("endTime") String endTime, @FormParam("priority") int priority, @FormParam("parameters") String parameters) {
        logger.info("提交job任务");
        try {
            // todo , 转换为 list
            List<DependencyEntry> dependEntryList = new ArrayList<DependencyEntry>();

            // 不为null且不为空字符串才处理
            if (dependJobIds != null && !dependJobIds.equals("") && !dependJobIds.equals("null")) {
                JSONObject dependIdsJson = new JSONObject(dependJobIds);
                for (Object key : dependIdsJson.keySet()) {
                    // String value = dependIdsJson.getString((String) key);
                    DependencyEntry entry = DependencyEntry.newBuilder().setJobId(Integer.parseInt((String) key)).build();
                }
            }

            // todo parameters 从json转化为 list
            List<MapEntryProtos.MapEntry> paraList = new ArrayList<MapEntryProtos.MapEntry>();
            // 不为null且不为空字符串才处理
            if (parameters != null && !parameters.equals("")) {
                JSONObject parameterJson = new JSONObject(parameters);
                for (Object key : parameterJson.keySet()) {
                    String value = parameterJson.getString((String) key);
                    MapEntryProtos.MapEntry entry = MapEntryProtos.MapEntry.newBuilder().setKey((String) key).setValue(value).build();
                    paraList.add(entry);
                }
            }
            Long startTimeLong = null;
            Long endTimeLong = null;
            if (startTime != null && !startTime.equals("")) {
                startTimeLong = dateTimeFormatter.parseDateTime(startTime).getMillis();
            }

            if (endTime != null && !endTime.equals("")) {
                endTimeLong = dateTimeFormatter.parseDateTime(endTime).getMillis();
            }

            // 构造新增任务请求
            RestServerSubmitJobRequest.Builder builder = RestServerSubmitJobRequest.newBuilder().setAppName(appName).setJobName(jobName)
                    .setAppKey(appKey).setCronExpression(cronExp).addAllDependencyEntry(dependEntryList).setUser(user).setJobType(jobType)
                    .setContent(content).setGroupId(groupId).setPriority(priority).setFailedRetries(failedRetries).setFailedInterval(failedInterval)
                    .setRejectRetries(rejectRetries).setRejectInterval(rejectInterval).addAllParameters(paraList);
            if (startTimeLong != null) {
                builder.setStartTime(startTimeLong);
            }
            if (endTimeLong != null) {
                builder.setEndTime(endTimeLong);
            }
            RestServerSubmitJobRequest request = builder.build();

            // 发送请求到server尝试新增

            ServerSubmitJobResponse response = (ServerSubmitJobResponse) callActor(AkkaType.server, request);

            // 判断是否新增成功
            if (response.getSuccess()) {
                JobVo vo = new JobVo();
                vo.setJobId(response.getJobId());
                return successResult(vo);
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            logger.error("", e);
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
    public RestResult edit(@FormParam("appName") String appName, @FormParam("appKey") String appKey, @FormParam("jobName") String jobName,
            @FormParam("jobId") Long jobId, @FormParam("cronExpression") String cronExp, @FormParam("dependJobIds") String dependJobIds,
            @FormParam("user") String user, @FormParam("jobType") String jobType, @FormParam("content") String content,
            @FormParam("groupId") int groupId, @FormParam("rejectRetries") int rejectRetries, @FormParam("rejectInterval") int rejectInterval,
            @FormParam("failedRetries") int failedRetries, @FormParam("failedInterval") int failedInterval, @FormParam("startTime") String startTime,
            @FormParam("endTime") String endTime, @FormParam("priority") int priority, @FormParam("parameters") String parameters) {

        try {
            logger.info("更新job任务");

            // todo , 转换为 list
            List<DependencyEntry> dependEntryList = new ArrayList<DependencyEntry>();
            // 不为null且不为空字符串才处理

            if (dependJobIds != null && !dependJobIds.equals("")) {
                JSONObject dependIdsJson = new JSONObject(dependJobIds);
                for (Object key : dependIdsJson.keySet()) {
                    String value = dependIdsJson.getString((String) key);
                    if (value.equalsIgnoreCase("add")) {
                        DependencyEntry entry = DependencyEntry.newBuilder().setJobId(Integer.parseInt((String) key))
                                .setOperator(DependencyEntry.DependencyOperator.ADD).build();
                        dependEntryList.add(entry);
                    }
                    if (value.equalsIgnoreCase("delete")) {
                        DependencyEntry entry = DependencyEntry.newBuilder().setJobId(Integer.parseInt((String) key))
                                .setOperator(DependencyEntry.DependencyOperator.REMOVE).build();
                        dependEntryList.add(entry);
                    }
                }
            }

            // todo parameters 从json转化为 list
            List<MapEntryProtos.MapEntry> paraList = new ArrayList<MapEntryProtos.MapEntry>();
            // 不为null且不为空字符串才处理
            if (parameters != null && !parameters.equals("")) {
                JSONObject parameterJson = new JSONObject(parameters);
                for (Object key : parameterJson.keySet()) {
                    String value = parameterJson.getString((String) key);
                    MapEntryProtos.MapEntry entry = MapEntryProtos.MapEntry.newBuilder().setKey((String) key).setValue(value).build();
                    paraList.add(entry);
                }
            }

            Long startTimeLong = null;
            Long endTimeLong = null;
            if (startTime != null && !startTime.equals("")) {
                startTimeLong = dateTimeFormatter.parseDateTime(startTime).getMillis();
            }
            if (endTime != null && !endTime.equals("")) {
                endTimeLong = dateTimeFormatter.parseDateTime(endTime).getMillis();
            }

            // 构造修改job基本信息请求
            RestServerModifyJobRequest request = null;
            RestServerModifyJobRequest.Builder builder = RestServerModifyJobRequest.newBuilder().setAppName(appName).setJobName(jobName)
                    .setJobId(jobId).setCronExpression(cronExp).setUser(user).setJobType(jobType).setContent(content).setGroupId(groupId)
                    .setPriority(priority).setFailedRetries(failedRetries).setFailedInterval(failedInterval).setRejectRetries(rejectRetries)
                    .setRejectInterval(rejectInterval).addAllParameters(paraList);

            if (startTimeLong != null) {
                builder.setStartTime(startTimeLong);
            }
            if (endTimeLong != null) {
                builder.setEndTime(endTimeLong);
            }
            request = builder.build();

            // 发送信息到server修改job基本信息
            ServerModifyJobResponse response = (ServerModifyJobResponse) callActor(AkkaType.server, request);

            // 判断修改基本信息是否成功，修改基本信息成功后才尝试修改依赖
            if (response.getSuccess()) {
                // 构造修改job依赖请求
                RestServerModifyDependencyRequest modifyDependencyRequest = RestServerModifyDependencyRequest.newBuilder().setJobId(jobId)
                        .addAllDependencyEntry(dependEntryList).build();
                // 发送信息到server修改依赖
                ServerModifyDependencyResponse dependencyResponse = (ServerModifyDependencyResponse) callActor(AkkaType.server,
                        modifyDependencyRequest);
                // 修改依赖是否成功
                if (dependencyResponse.getSuccess()) {
                    return successResult();
                } else {
                    return errorResult(dependencyResponse.getMessage());
                }
            }
            // 修改基本信息出错
            else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getStackTrace());
            return errorResult(e.getMessage());
        }
    }

    /**
     * 修改job任务状态
     *
     * @throws Exception
     */
    @POST
    @Path("flag")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult flag(@FormParam("jobId") Long jobId, @FormParam("appKey") String appKey, @FormParam("appName") String appName,
            @FormParam("jobFlag") Integer jobFlag) throws Exception {
        try {
            // 构造删除job请求request，1.启用2.禁用3.过期4.垃圾箱
            RestServerModifyJobFlagRequest request = RestServerModifyJobFlagRequest.newBuilder().setJobId(jobId).setJobFlag(jobFlag).build();
            // 发送请求到server尝试常熟
            ServerModifyJobFlagResponse response = (ServerModifyJobFlagResponse) callActor(AkkaType.server, request);

            // 判断删除是否成功
            if (response.getSuccess()) {
                JobVo vo = new JobVo();
                vo.setJobId(jobId);
                return successResult(vo);
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getStackTrace());
            return errorResult(e.getMessage());
        }
    }

    /**
     * 重跑任务
     *
     * @throws Exception
     */
    @POST
    @Path("rerun")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult rerun(@FormParam("originJobId") Long originJobId, @FormParam("startTime") String startTime,
            @FormParam("endTime") String endTime, @FormParam("reRunJobs") String reRunJobs) throws Exception {
        JSONArray reRunJobArr = new JSONArray(reRunJobs);
        boolean hasSelf = false;
        for (int i = 0; i < reRunJobArr.length(); i++) {
            if (originJobId.equals(reRunJobArr.getLong(i))) {
                hasSelf = true;
                break;
            }
        }
        if (!hasSelf) {
            reRunJobArr.put(originJobId);
        }

        boolean flag = true;
        JSONObject msg = new JSONObject();
        for (int i = 0; i < reRunJobArr.length(); i++) {
            Long singleOriginId = reRunJobArr.getLong(i);
            // 构造新增任务请求
            RestServerSubmitJobRequest request = RestServerSubmitJobRequest.newBuilder().setOriginJobId(singleOriginId).build();
            ServerSubmitJobResponse response = (ServerSubmitJobResponse) callActor(AkkaType.server, request);

            // 保存整理而言是否成功，如果某个job重跑失败，则算失败
            flag = flag && response.getSuccess();
            // 判断是否重跑成功
            if (response.getSuccess()) {
                msg.put(singleOriginId.toString(), "success," + response.getMessage());
            } else {
                msg.put(singleOriginId.toString(), "failed," + response.getMessage());
            }
        }

        // 判断删除是否成功
        if (flag) {
            return new RestResult(MsgCode.SUCCESS);
        } else {
            return errorResult(msg.toString());
        }
    }

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