/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月3日 下午1:49:42
 */

package com.mogujie.jarvis.rest.controller;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.KillTaskProtos.RestServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskResponse;
import com.mogujie.jarvis.protocol.ManualRerunTaskProtos.RestServerManualRerunTaskRequest;
import com.mogujie.jarvis.protocol.ManualRerunTaskProtos.ServerManualRerunTaskResponse;
import com.mogujie.jarvis.protocol.RetryTaskProtos.RestServerRetryTaskRequest;
import com.mogujie.jarvis.protocol.RetryTaskProtos.ServerRetryTaskResponse;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitTaskResponse;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.utils.JsonParameters;
import com.mogujie.jarvis.rest.vo.RerunTaskVo;
import com.mogujie.jarvis.rest.vo.TaskEntryVo;
import com.mogujie.jarvis.rest.vo.TaskVo;

/**
 * @author guangming
 *
 */
@Path("api/task")
public class TaskController extends AbstractController {
    @POST
    @Path("kill")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> kill(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JSONObject para = new JSONObject(parameters);
            long jobId = para.getLong("jobId");
            long taskId = para.getLong("taskId");
            int attemptId = para.getInt("attemptId");
            String fullId = IdUtils.getFullId(jobId, taskId, attemptId);

            RestServerKillTaskRequest request = RestServerKillTaskRequest.newBuilder().setAppAuth(appAuth).setFullId(fullId).build();

            ServerKillTaskResponse response = (ServerKillTaskResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return errorResult(e.getMessage());
        }
    }

    @POST
    @Path("retry")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> retry(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JSONObject para = new JSONObject(parameters);
            long taskId = para.getLong("taskId");

            RestServerRetryTaskRequest request = RestServerRetryTaskRequest.newBuilder().setAppAuth(appAuth).setTaskId(taskId).build();

            ServerRetryTaskResponse response = (ServerRetryTaskResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return errorResult(e.getMessage());
        }
    }

    @POST
    @Path("rerun")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> rerun(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            RerunTaskVo rerunVo = JsonParameters.fromJson(parameters, RerunTaskVo.class);
            List<Long> jobIdList = rerunVo.getJobIdList();
            long startDate = rerunVo.getStartDate();
            long endDate = rerunVo.getEndDate();
            boolean runChild = rerunVo.isRunChild();

            RestServerManualRerunTaskRequest.Builder builder = RestServerManualRerunTaskRequest.newBuilder();
            for (long jobId : jobIdList) {
                builder.addJobId(jobId);
            }
            RestServerManualRerunTaskRequest request = builder.setAppAuth(appAuth).setStartTime(startDate).setEndTime(endDate).setRunChild(runChild)
                    .build();

            ServerManualRerunTaskResponse response = (ServerManualRerunTaskResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return errorResult(e.getMessage());
        }
    }

    @POST
    @Path("submit")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> submit(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            TaskEntryVo taskVo = JsonParameters.fromJson(parameters, TaskEntryVo.class);
            if (taskVo.getParams() != null) {
                JsonParameters.toJson(taskVo.getParams(), List.class);
            }
            RestServerSubmitTaskRequest request = RestServerSubmitTaskRequest.newBuilder().setAppAuth(appAuth).setTaskName(taskVo.getTaskName())
                    .setContent(taskVo.getContent()).setTaskType(taskVo.getTaskType()).setUser(taskVo.getUser()).setGroupId(taskVo.getGroupId())
                    .setPriority(taskVo.getPriority(0)).setRejectRetries(taskVo.getRejectRetries(0)).setRejectInterval(taskVo.getRejectInterval(3))
                    .setFailedRetries(taskVo.getFailedRetries(0)).setFailedInterval(taskVo.getFailedInterval(3)).build();

            ServerSubmitTaskResponse response = (ServerSubmitTaskResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                TaskVo vo = new TaskVo();
                vo.setTaskId(response.getTaskId());
                return successResult(vo);
            } else {
                return errorResult(response.getMessage());
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return errorResult(e.getMessage());
        }
    }
}
