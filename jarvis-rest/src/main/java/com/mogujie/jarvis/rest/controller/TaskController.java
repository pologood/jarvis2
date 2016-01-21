/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月3日 下午1:49:42
 */

package com.mogujie.jarvis.rest.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.domain.JobRelationType;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.KillTaskProtos.RestServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskResponse;
import com.mogujie.jarvis.protocol.ManualRerunTaskProtos.RestServerManualRerunTaskRequest;
import com.mogujie.jarvis.protocol.ManualRerunTaskProtos.ServerManualRerunTaskResponse;
import com.mogujie.jarvis.protocol.ModifyTaskStatusProtos.RestServerModifyTaskStatusRequest;
import com.mogujie.jarvis.protocol.ModifyTaskStatusProtos.ServerModifyTaskStatusResponse;
import com.mogujie.jarvis.protocol.QueryTaskRelationProtos.RestServerQueryTaskRelationRequest;
import com.mogujie.jarvis.protocol.QueryTaskRelationProtos.ServerQueryTaskRelationResponse;
import com.mogujie.jarvis.protocol.QueryTaskRelationProtos.TaskMapEntry;
import com.mogujie.jarvis.protocol.RetryTaskProtos.RestServerRetryTaskRequest;
import com.mogujie.jarvis.protocol.RetryTaskProtos.ServerRetryTaskResponse;
import com.mogujie.jarvis.protocol.RemoveTaskProtos.RestServerRemoveTaskRequest;
import com.mogujie.jarvis.protocol.RemoveTaskProtos.ServerRemoveTaskResponse;

import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.utils.JsonParameters;
import com.mogujie.jarvis.rest.vo.TaskRerunVo;
import com.mogujie.jarvis.rest.vo.TaskRelationsVo;

/**
 * @author guangming
 */
@Path("api/task")
public class TaskController extends AbstractController {

    /**
     * 根据fullId kill task
     *
     * @throws Exception
     */
    @POST
    @Path("kill")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult kill(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters para = new JsonParameters(parameters);
            long jobId = para.getLongNotNull("jobId");
            long taskId = para.getLongNotNull("taskId");
            int attemptId = para.getIntegerNotNull("attemptId");
            String fullId = IdUtils.getFullId(jobId, taskId, attemptId);

            RestServerKillTaskRequest request = RestServerKillTaskRequest.newBuilder().setAppAuth(appAuth).setFullId(fullId).build();

            ServerKillTaskResponse response = (ServerKillTaskResponse) callActor(AkkaType.SERVER, request);
            return response.getSuccess() ? successResult() : errorResult(response.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e);
        }
    }

    /**
     * 根据taskId原地重试task，按照历史依赖关系
     *
     * @throws Exception
     */
    @POST
    @Path("retry")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult retry(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters para = new JsonParameters(parameters);
            long taskId = para.getLongNotNull("taskId");

            RestServerRetryTaskRequest request = RestServerRetryTaskRequest.newBuilder().setAppAuth(appAuth).setTaskId(taskId).build();

            ServerRetryTaskResponse response = (ServerRetryTaskResponse) callActor(AkkaType.SERVER, request);
            return response.getSuccess() ? successResult() : errorResult(response.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e);
        }
    }

    /**
     * 给定jobId和一段时间，手动重跑任务，按照新的依赖关系，支持是否重跑后续任务
     *
     * @throws Exception
     */
    @POST
    @Path("rerun")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult rerun(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            TaskRerunVo rerunVo = JsonHelper.fromJson(parameters, TaskRerunVo.class);
            List<Long> jobIdList = rerunVo.getJobIdList();
            long startDate = rerunVo.getStartDate();
            long endDate = rerunVo.getEndDate();

            RestServerManualRerunTaskRequest.Builder builder = RestServerManualRerunTaskRequest.newBuilder();
            for (long jobId : jobIdList) {
                builder.addJobId(jobId);
            }
            RestServerManualRerunTaskRequest request = builder.setAppAuth(appAuth).setStartTime(startDate).setEndTime(endDate).build();

            ServerManualRerunTaskResponse response = (ServerManualRerunTaskResponse) callActor(AkkaType.SERVER, request);
            return response.getSuccess() ? successResult() : errorResult(response.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e);
        }
    }


    /**
     * 移除Task
     *
     * @throws Exception
     */
    @POST
    @Path("remove")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult remove(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
                            @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters para = new JsonParameters(parameters);
            long taskId = para.getLongNotNull("taskId");

            RestServerRemoveTaskRequest request = RestServerRemoveTaskRequest.newBuilder().setAppAuth(appAuth).setTaskId(taskId).build();

            ServerRemoveTaskResponse response = (ServerRemoveTaskResponse) callActor(AkkaType.SERVER, request);
            return response.getSuccess() ? successResult() : errorResult(response.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e);
        }
    }



    /**
     * 强制修改task的状态（慎用！！仅限管理员使用！！）
     *
     * @throws Exception
     */
    @POST
    @Path("modify/status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult modifyStatus(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters para = new JsonParameters(parameters);
            long taskId = para.getLongNotNull("taskId");
            int status = para.getIntegerNotNull("status");

            RestServerModifyTaskStatusRequest request = RestServerModifyTaskStatusRequest.newBuilder().setAppAuth(appAuth).setTaskId(taskId)
                    .setStatus(status).build();

            ServerModifyTaskStatusResponse response = (ServerModifyTaskStatusResponse) callActor(AkkaType.SERVER, request);
            return response.getSuccess() ? successResult() : errorResult(response.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e);
        }
    }

    /**
     * 查找Job关系
     */
    @POST
    @Path("queryRelation")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult queryRelation(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters paras = new JsonParameters(parameters);
            Long jobId = paras.getLongNotNull("taskId");
            Integer relationType = paras.getIntegerNotNull("relationType");
            if (!JobRelationType.isValid(relationType)) {
                throw new IllegalArgumentException("relationType 内容不对. value:" + relationType);
            }

            RestServerQueryTaskRelationRequest request = RestServerQueryTaskRelationRequest.newBuilder().setAppAuth(appAuth).setTaskId(jobId)
                    .setRelationType(relationType).build();

            ServerQueryTaskRelationResponse response = (ServerQueryTaskRelationResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                TaskRelationsVo vo = new TaskRelationsVo();
                if (response.getTaskRelationMapList() != null) {
                    List<TaskRelationsVo.RelationEntry> list = new ArrayList<>();
                    for (TaskMapEntry entry : response.getTaskRelationMapList()) {
                        list.add(new TaskRelationsVo.RelationEntry().setJobId(entry.getJobId()).setTaskIds(entry.getTaskIdList()));
                    }
                    vo.setList(list);
                }
                return successResult(vo);
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e);
        }
    }

}
