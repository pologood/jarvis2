/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月15日 上午10:45:07
 */

package com.mogujie.jarvis.rest.controller;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;

import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.expression.ScheduleExpressionType;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.protocol.JobScheduleExpressionEntryProtos.ScheduleExpressionEntry;
import com.mogujie.jarvis.protocol.KillTaskProtos.RestServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskResponse;
import com.mogujie.jarvis.protocol.QueryTaskByJobIdProtos.RestServerQueryTaskByJobIdRequest;
import com.mogujie.jarvis.protocol.QueryTaskByJobIdProtos.ServerQueryTaskByJobIdResponse;
import com.mogujie.jarvis.protocol.QueryTaskByJobIdProtos.TaskEntry;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.utils.ValidUtils;
import com.mogujie.jarvis.rest.utils.ValidUtils.CheckMode;
import com.mogujie.jarvis.rest.vo.JobResultVo;
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
    public RestResult executeSql(@FormParam("token") String appToken, @FormParam("name") String appName, @FormParam("time") long time,
            @FormParam("content") String content, @FormParam("executor") String user, @FormParam("jobName") String jobName,
            @FormParam("jobType") String jobType, @FormParam("groupId") Integer groupId) {
        LOGGER.debug("提交job任务");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JobVo jobVo = new JobVo();
            jobVo.setJobName(jobName);
            jobVo.setJobType(jobName);
            jobVo.setWorkerGroupId(groupId);
            jobVo.setContent(content);
            jobVo.setTemp(true);
            ValidUtils.checkJob(CheckMode.ADD, jobVo);
            RestSubmitJobRequest request = vo2RequestByAdd(jobVo, appAuth, user);

            // 发送请求到server
            ServerSubmitJobResponse response = (ServerSubmitJobResponse) callActor(AkkaType.SERVER, request);
            return response.getSuccess() ? successResult(new JobResultVo().setJobId(response.getJobId()))
                    : errorResult(response.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e);
        }
    }

    @POST
    @Path("killjob")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult killJob(@FormParam("token") String appToken, @FormParam("name") String appName, @FormParam("time") long time,
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
                    return errorResult(err);
                } else {
                    long taskId = taskEntryList.get(0).getTaskId();
                    int attemptId = taskEntryList.get(0).getAttemptId();
                    String fullId = IdUtils.getFullId(jobId, taskId, attemptId);
                    RestServerKillTaskRequest request = RestServerKillTaskRequest.newBuilder().setAppAuth(appAuth).setFullId(fullId).build();
                    ServerKillTaskResponse response = (ServerKillTaskResponse) callActor(AkkaType.SERVER, request);
                    return response.getSuccess() ? successResult() : errorResult(response.getMessage());
                }
            } else {
                return errorResult(queryTaskReponse.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e);
        }
    }

    @POST
    @Path("querylog")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult queryLog(@FormParam("token") String appToke, @FormParam("name") String appName, @FormParam("time") long time,
            @FormParam("jobId") String jobId) {
        //TODO
        return null;
    }

    @POST
    @Path("jobstatus")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult queryJobStatus(@FormParam("token") String appKey, @FormParam("name") String appName, @FormParam("time") long time,
            @FormParam("jobId") String jobId) {
        //TODO
        return null;
    }

    @POST
    @Path("/result")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult jobStatus(@FormParam("jobId") Integer jobId,
            @FormParam(value = "volume") String volume) {
        //TODO
        return null;
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

        DateTime now = DateTime.now();
        String cronExpression = new StringBuilder().append(now.getSecondOfMinute()) //秒
                .append(" ").append(now.getMinuteOfHour()) //分
                .append(" ").append(now.getHourOfDay()) //时
                .append(" ").append(now.getDayOfMonth()) //天
                .append(" ").append(now.getMonthOfYear()) //月
                .append(" ?") //周
                .append(" ").append(now.getYear()) //年
                .toString();
        ScheduleExpressionEntry entry = ScheduleExpressionEntry.newBuilder().setOperator(OperationMode.ADD.getValue())
                .setExpressionId(0)
                .setExpressionType(ScheduleExpressionType.CRON.getValue())
                .setScheduleExpression(cronExpression)
                .build();
        builder.addExpressionEntry(entry);

        return builder.build();
    }
}
