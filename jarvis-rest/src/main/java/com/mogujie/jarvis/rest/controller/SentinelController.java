/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月15日 上午10:45:07
 */

package com.mogujie.jarvis.rest.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.collect.Maps;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.protocol.KillTaskProtos.RestServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskResponse;
import com.mogujie.jarvis.protocol.LogProtos.LogStorageReadLogResponse;
import com.mogujie.jarvis.protocol.LogProtos.RestReadLogRequest;
import com.mogujie.jarvis.protocol.QueryTaskByJobIdProtos.RestServerQueryTaskByJobIdRequest;
import com.mogujie.jarvis.protocol.QueryTaskByJobIdProtos.ServerQueryTaskByJobIdResponse;
import com.mogujie.jarvis.protocol.QueryTaskByJobIdProtos.TaskEntry;
import com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest;
import com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse;
import com.mogujie.jarvis.rest.sentinel.BaseRet;
import com.mogujie.jarvis.rest.sentinel.JobStatusEnum;
import com.mogujie.jarvis.rest.sentinel.LogQueryRet;
import com.mogujie.jarvis.rest.sentinel.ResponseCodeEnum;
import com.mogujie.jarvis.rest.sentinel.ResponseParams;
import com.mogujie.jarvis.rest.utils.ValidUtils;
import com.mogujie.jarvis.rest.utils.ValidUtils.CheckMode;
import com.mogujie.jarvis.rest.vo.JobVo;

/**
 * 兼容sentinel rest接口
 *
 * @author guangming
 *
 */
@Deprecated
@Path("server")
public class SentinelController extends AbstractController {

    private final static int DEFAULT_SIZE = 10000;
    private static Map<String, Long> logOffsetMap = Maps.newConcurrentMap();

    /**
     * @param appToken
     * @param appName 业务系统名称
     * @param time
     * @param content
     * @param user
     * @param jobName
     * @param jobType
     * @param groupId
     * @return RestResult
     */
    @POST
    @Path("execute")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseParams executeSql(@FormParam("token") String appToken, @FormParam("name") String appName, @FormParam("time") long time,
            @FormParam("content") String content, @FormParam("executor") String user, @FormParam("jobName") String jobName,
            @FormParam("jobType") String jobType, @FormParam("groupId") Integer groupId) {
        LOGGER.debug("提交job任务");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JobVo jobVo = new JobVo();
            jobVo.setJobName(jobName);
            jobVo.setJobType(jobType);
            jobVo.setWorkerGroupId(groupId);
            jobVo.setContent(content);
            jobVo.setTemp(true);
            ValidUtils.checkJob(CheckMode.ADD, jobVo);
            RestSubmitJobRequest request = vo2RequestByAdd(jobVo, appAuth, user);

            // 发送请求到server
            ServerSubmitJobResponse response = (ServerSubmitJobResponse) callActor(AkkaType.SERVER, request);
            BaseRet result;
            if (response.getSuccess()) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("jobId", response.getJobId());
                result = new BaseRet(ResponseCodeEnum.SUCCESS, "任务提交成功", params);
            } else {
                result = new BaseRet(ResponseCodeEnum.FAILED, "任务添加失败:" + response.getMessage());
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("", e);
            return new BaseRet(ResponseCodeEnum.FAILED, "任务添加失败:" + e.getMessage());
        }
    }

    @POST
    @Path("killjob")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseParams killJob(@FormParam("token") String appToken, @FormParam("name") String appName, @FormParam("time") long time,
            @FormParam("jobId") long jobId) {
        LOGGER.debug("kill task");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            RestServerQueryTaskByJobIdRequest queryTaskRequest = RestServerQueryTaskByJobIdRequest.newBuilder().setAppAuth(appAuth)
                    .setAppAuth(appAuth).setJobId(jobId).build();
            ServerQueryTaskByJobIdResponse queryTaskReponse = (ServerQueryTaskByJobIdResponse) callActor(AkkaType.SERVER, queryTaskRequest);
            if (queryTaskReponse.getSuccess()) {
                List<TaskEntry> taskEntryList = queryTaskReponse.getTaskEntryList();
                if (taskEntryList == null || taskEntryList.size() != 1) {
                    String err = "job[" + jobId + "] 尚未调度起来";
                    return new BaseRet(ResponseCodeEnum.FAILED, err);
                } else {
                    long taskId = taskEntryList.get(0).getTaskId();
                    int attemptId = taskEntryList.get(0).getAttemptId();
                    String fullId = IdUtils.getFullId(jobId, taskId, attemptId);
                    RestServerKillTaskRequest request = RestServerKillTaskRequest.newBuilder().setAppAuth(appAuth).setFullId(fullId).build();
                    ServerKillTaskResponse response = (ServerKillTaskResponse) callActor(AkkaType.SERVER, request);
                    if (response.getSuccess()) {
                        return new BaseRet(ResponseCodeEnum.SUCCESS, "jobId: " + jobId + " 任务删除成功");
                    } else {
                        return new BaseRet(ResponseCodeEnum.FAILED, response.getMessage());
                    }
                }
            } else {
                return new BaseRet(ResponseCodeEnum.FAILED, queryTaskReponse.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            return new BaseRet(ResponseCodeEnum.FAILED, e.getMessage());
        }
    }

    @POST
    @Path("jobstatus")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseParams queryJobStatus(@FormParam("token") String appToken, @FormParam("name") String appName, @FormParam("time") long time,
            @FormParam("jobId") long jobId) {
        LOGGER.debug("query job status");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            RestServerQueryTaskByJobIdRequest queryTaskRequest = RestServerQueryTaskByJobIdRequest.newBuilder().setAppAuth(appAuth)
                    .setAppAuth(appAuth).setJobId(jobId).build();
            ServerQueryTaskByJobIdResponse queryTaskReponse = (ServerQueryTaskByJobIdResponse) callActor(AkkaType.SERVER, queryTaskRequest);
            if (queryTaskReponse.getSuccess()) {
                List<TaskEntry> taskEntryList = queryTaskReponse.getTaskEntryList();
                if (taskEntryList == null || taskEntryList.size() != 1) {
                    String err = "job[" + jobId + "] 尚未调度起来";
                    return new BaseRet(ResponseCodeEnum.FAILED, err);
                } else {
                    long taskId = taskEntryList.get(0).getTaskId();
                    RestServerQueryTaskStatusRequest request = RestServerQueryTaskStatusRequest.newBuilder()
                            .setAppAuth(appAuth).setTaskId(taskId).build();

                    ServerQueryTaskStatusResponse response = (ServerQueryTaskStatusResponse) callActor(AkkaType.SERVER, request);
                    if (response.getSuccess()) {
                        TaskStatus jarvisStatus = TaskStatus.parseValue(response.getStatus());
                        JobStatusEnum sentinelStatus = convert2SentinelStatus(jarvisStatus);
                        if (sentinelStatus == null) {
                            return new BaseRet(ResponseCodeEnum.FAILED, "无法转换到sentinel的状态，jarvis状态为: " + jarvisStatus);
                        } else {
                            BaseRet ret = new BaseRet(ResponseCodeEnum.SUCCESS);
                            ret.put("jobStatus", sentinelStatus.getValue());
                            ret.put("message", "查询状态成功");
                            return ret;
                        }
                    } else {
                        return new BaseRet(ResponseCodeEnum.FAILED, response.getMessage());
                    }
                }
            } else {
                return new BaseRet(ResponseCodeEnum.FAILED, queryTaskReponse.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            return new BaseRet(ResponseCodeEnum.FAILED, e.getMessage());
        }
    }

    @POST
    @Path("result")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseParams queryResult(@FormParam("token") String appToken, @FormParam("name") String appName,
            @FormParam("time") long time, @FormParam("jobId") Integer jobId) {
        LOGGER.debug("query result");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            RestServerQueryTaskByJobIdRequest queryTaskRequest = RestServerQueryTaskByJobIdRequest.newBuilder().setAppAuth(appAuth)
                    .setAppAuth(appAuth).setJobId(jobId).build();
            ServerQueryTaskByJobIdResponse queryTaskReponse = (ServerQueryTaskByJobIdResponse) callActor(AkkaType.SERVER, queryTaskRequest);
            if (queryTaskReponse.getSuccess()) {
                List<TaskEntry> taskEntryList = queryTaskReponse.getTaskEntryList();
                if (taskEntryList == null || taskEntryList.size() != 1) {
                    String err = "job[" + jobId + "] 尚未调度起来";
                    return new BaseRet(ResponseCodeEnum.FAILED, err);
                } else {
                    long taskId = taskEntryList.get(0).getTaskId();
                    int attemptId = taskEntryList.get(0).getAttemptId();
                    String fullId = IdUtils.getFullId(jobId, taskId, attemptId);
                    long offset = 0;
                    int lines = DEFAULT_SIZE;

                    RestReadLogRequest request = RestReadLogRequest.newBuilder()
                            .setAppAuth(appAuth)
                            .setFullId(fullId)
                            .setType(StreamType.STD_OUT.getValue())
                            .setOffset(offset)
                            .setSize(lines)
                            .build();

                    LogStorageReadLogResponse response = (LogStorageReadLogResponse) callActor(AkkaType.LOGSTORAGE, request);
                    if (response.getSuccess()) {
                        String result = response.getLog();
                        while (!response.getIsEnd()) {
                            offset = response.getOffset();
                            request = RestReadLogRequest.newBuilder()
                                    .setAppAuth(appAuth)
                                    .setFullId(fullId)
                                    .setType(StreamType.STD_OUT.getValue())
                                    .setOffset(offset)
                                    .setSize(lines)
                                    .build();
                            response = (LogStorageReadLogResponse) callActor(AkkaType.LOGSTORAGE, request);
                            if (!response.getSuccess()) {
                                return new BaseRet(ResponseCodeEnum.FAILED, response.getMessage());
                            } else {
                                result += response.getLog();
                            }
                        }
                        String[] rows = result.split("\n", -1);
                        rows = Arrays.copyOf(rows, rows.length - 1);
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put("data", rows);
                        map.put("isEnd", true);
                        map.put("rowCnt", rows.length);
                        map.put("volume","");
                        return new BaseRet(ResponseCodeEnum.SUCCESS, "成功", map);
                    } else {
                        return new BaseRet(ResponseCodeEnum.FAILED, response.getMessage());
                    }
                }
            } else {
                return new BaseRet(ResponseCodeEnum.FAILED, queryTaskReponse.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            return new BaseRet(ResponseCodeEnum.FAILED, e.getMessage());
        }
    }

    @POST
    @Path("querylog")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseParams queryLog(@FormParam("token") String appToken, @FormParam("name") String appName, @FormParam("time") long time,
            @FormParam("jobId") long jobId) {
        LOGGER.debug("query job status");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            RestServerQueryTaskByJobIdRequest queryTaskRequest = RestServerQueryTaskByJobIdRequest.newBuilder().setAppAuth(appAuth)
                    .setAppAuth(appAuth).setJobId(jobId).build();
            ServerQueryTaskByJobIdResponse queryTaskReponse = (ServerQueryTaskByJobIdResponse) callActor(AkkaType.SERVER, queryTaskRequest);
            if (queryTaskReponse.getSuccess()) {
                List<TaskEntry> taskEntryList = queryTaskReponse.getTaskEntryList();
                if (taskEntryList == null || taskEntryList.size() != 1) {
                    String err = "job[" + jobId + "] 尚未调度起来";
                    return new BaseRet(ResponseCodeEnum.FAILED, err);
                } else {
                    long taskId = taskEntryList.get(0).getTaskId();
                    int attemptId = taskEntryList.get(0).getAttemptId();
                    String fullId = IdUtils.getFullId(jobId, taskId, attemptId);
                    long offset = logOffsetMap.containsKey(fullId) ? logOffsetMap.get(fullId) : 0;
                    int lines = DEFAULT_SIZE;

                    RestReadLogRequest request = RestReadLogRequest.newBuilder()
                            .setAppAuth(appAuth)
                            .setFullId(fullId)
                            .setType(StreamType.STD_ERR.getValue())
                            .setOffset(offset)
                            .setSize(lines)
                            .build();

                    LogStorageReadLogResponse response = (LogStorageReadLogResponse) callActor(AkkaType.LOGSTORAGE, request);
                    if (response.getSuccess()) {
                        LogQueryRet ret = new LogQueryRet(ResponseCodeEnum.SUCCESS, "success");
                        ret.setLog(response.getLog());
                        if (response.getIsEnd()) {
                            ret.setIsEnd(true);
                            logOffsetMap.remove(fullId);
                        } else {
                            logOffsetMap.put(fullId, response.getOffset());
                        }
                        return ret;
                    } else {
                        return new LogQueryRet(ResponseCodeEnum.FAILED, response.getMessage());
                    }
                }
            } else {
                return new LogQueryRet(ResponseCodeEnum.FAILED, queryTaskReponse.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            return new LogQueryRet(ResponseCodeEnum.FAILED, e.getMessage());
        }
    }

    /**
     * jobVo转换为request——增加
     */
    private RestSubmitJobRequest vo2RequestByAdd(JobVo vo, AppAuth appAuth, String user) {
        // 构造请求
        RestSubmitJobRequest.Builder builder = RestSubmitJobRequest.newBuilder().setAppAuth(appAuth).setUser(user)
                .setJobName(vo.getJobName())
                .setJobType(vo.getJobType())
                .setStatus(vo.getStatus(JobStatus.ENABLE.getValue()))
                .setContent(vo.getContent())
                .setParameters(vo.getParams("{}"))
                .setAppName(vo.getAppName(appAuth.getName()))
                .setWorkerGroupId(vo.getWorkerGroupId())
                .setBizGroupId(JarvisConstants.BIZ_GROUP_ID_UNKNOWN)
                .setPriority(vo.getPriority(1))
                .setIsTemp(vo.isTemp())
                .setActiveStartTime(vo.getActiveStartTime(0L))
                .setActiveEndTime(vo.getActiveEndTime(0L))
                .setExpiredTime(vo.getExpiredTime(60*10)) //临时任务默认十分钟
                .setFailedAttempts(vo.getFailedAttempts(0))
                .setFailedInterval(vo.getFailedInterval(3));

//        DateTime now = DateTime.now();
//        String cronExpression = new StringBuilder().append(now.getSecondOfMinute()) //秒
//                .append(" ").append(now.getMinuteOfHour()) //分
//                .append(" ").append(now.getHourOfDay()) //时
//                .append(" ").append(now.getDayOfMonth()) //天
//                .append(" ").append(now.getMonthOfYear()) //月
//                .append(" ?") //周
//                .append(" ").append(now.getYear()) //年
//                .toString();
//        ScheduleExpressionEntry entry = ScheduleExpressionEntry.newBuilder().setOperator(OperationMode.ADD.getValue())
//                .setExpressionId(0)
//                .setExpressionType(ScheduleExpressionType.CRON.getValue())
//                .setScheduleExpression(cronExpression)
//                .build();
//        builder.addExpressionEntry(entry);

        return builder.build();
    }

    private JobStatusEnum convert2SentinelStatus(TaskStatus jarvisStatus) {
        JobStatusEnum sentinelStatus = null;
        if (jarvisStatus.equals(TaskStatus.SUCCESS)) {
            sentinelStatus = JobStatusEnum.SUCCESS;
        } else if (jarvisStatus.equals(TaskStatus.WAITING)) {
            sentinelStatus = JobStatusEnum.WAIT;
        } else if (jarvisStatus.equals(TaskStatus.READY)) {
            sentinelStatus = JobStatusEnum.WAIT;
        } else if (jarvisStatus.equals(TaskStatus.RUNNING)) {
            sentinelStatus = JobStatusEnum.RUNNING;
        } else if (jarvisStatus.equals(TaskStatus.FAILED)) {
            sentinelStatus = JobStatusEnum.FAIL;
        } else if (jarvisStatus.equals(TaskStatus.KILLED)) {
            sentinelStatus = JobStatusEnum.KILLED;
        } else if (jarvisStatus.equals(TaskStatus.REMOVED)) {
            sentinelStatus = JobStatusEnum.EXCEPTION;
        }
        return sentinelStatus;
    }
}
