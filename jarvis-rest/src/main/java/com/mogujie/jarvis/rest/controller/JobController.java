/*
 * 蘑菇街 Inc. Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming Create Date: 2015年10月08日 下午3:19:28
 */
package com.mogujie.jarvis.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Preconditions;
import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.domain.JobRelationType;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.JobProtos.JobStatusEntry;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobDependRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobScheduleExpRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobStatusRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestQueryJobRelationRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobDependResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobScheduleExpResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobStatusResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerQueryJobRelationResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.utils.ConvertValidUtils;
import com.mogujie.jarvis.rest.utils.JsonParameters;
import com.mogujie.jarvis.rest.vo.JobEntryVo;
import com.mogujie.jarvis.rest.vo.JobRelationsVo;
import com.mogujie.jarvis.rest.vo.JobVo;
import com.mogujie.jarvis.rest.vo.JobDependencyVo;
import com.mogujie.jarvis.rest.vo.JobScheduleExpVo;

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
    public RestResult submit(@FormParam("appName") String appName, @FormParam("appToken") String appToken, @FormParam("user") String user,
                             @FormParam("parameters") String parameters) {

        LOGGER.debug("提交job任务");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JobEntryVo jobVo = JsonHelper.fromJson(parameters, JobEntryVo.class);
            // parameters处理
            String jobParameters = "";
            if (jobVo.getParams() != null) {
                jobParameters = JsonHelper.toJson(jobVo.getParams(), Map.class);
            }

            // 构造请求
            RestSubmitJobRequest.Builder builder = RestSubmitJobRequest.newBuilder().setAppAuth(appAuth).setUser(user).setJobName(jobVo.getJobName())
                    .setJobType(jobVo.getJobType()).setStatus(jobVo.getStatus()).setContent(jobVo.getContent()).setParameters(jobParameters)
                    .setAppName(jobVo.getAppName(appName)).setWorkerGroupId(jobVo.getWorkerGroupId()).setPriority(jobVo.getPriority(1))
                    .setActiveStartTime(jobVo.getActiveStartTime(0L)).setActiveEndTime(jobVo.getActiveEndTime(0L)).setExpiredTime(600)
                    .setFailedAttempts(jobVo.getFailedAttempts(0)).setFailedInterval(jobVo.getFailedInterval(3));

            if (jobVo.getScheduleExpressionList() != null && jobVo.getScheduleExpressionList().size() > 0) {
                for (JobScheduleExpVo.ScheduleExpressionEntry entryInput : jobVo.getScheduleExpressionList()) {
                    builder.addExpressionEntry(ConvertValidUtils.ConvertScheduleExpressionEntry(entryInput));
                }
            }

            if (jobVo.getDependencyList() != null && jobVo.getDependencyList().size() > 0) {
                for (JobDependencyVo.DependencyEntry entryInput : jobVo.getDependencyList()) {
                    builder.addDependencyEntry(ConvertValidUtils.ConvertDependencyEntry(entryInput));
                }
            }

            // 发送请求到server
            ServerSubmitJobResponse response = (ServerSubmitJobResponse) callActor(AkkaType.SERVER, builder.build());

            // 判断是否成功
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

        LOGGER.debug("更新job任务");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JobEntryVo jobVo = JsonHelper.fromJson(parameters, JobEntryVo.class);

            // 构造请求
            RestModifyJobRequest.Builder builder = RestModifyJobRequest.newBuilder().setAppAuth(appAuth).setUser(user).setJobId(jobVo.getJobId());

            if (jobVo.getJobName() != null && !jobVo.getJobName().equals("")) {
                builder.setJobName(jobVo.getJobName());
            }
            if (jobVo.getJobType() != null) {
                builder.setJobType(jobVo.getJobType());
            }
            if (jobVo.getContent() != null && !jobVo.getContent().equals("")) {
                builder.setContent(jobVo.getContent());
            }
            if (jobVo.getParams() != null) {
                String jobParameters = JsonHelper.toJson(jobVo.getParams(), Map.class);
                builder.setParameters(jobParameters);
            }
            if (jobVo.getPriority() != null) {
                builder.setPriority(jobVo.getPriority());
            }
            if (jobVo.getAppName() != null) {
                builder.setAppName(jobVo.getAppName());
            }
            if (jobVo.getWorkerGroupId() != null) {
                builder.setWorkerGroupId(jobVo.getWorkerGroupId());
            }
            if (jobVo.getActiveStartTime() != null) {
                builder.setActiveStartTime(jobVo.getActiveStartTime());
            }
            if (jobVo.getActiveEndTime() != null) {
                builder.setActiveEndTime(jobVo.getActiveEndTime());
            }
            if (jobVo.getExpiredTime() != null) {
                builder.setExpiredTime(jobVo.getExpiredTime());
            }
            if (jobVo.getFailedAttempts() != null) {
                builder.setFailedAttempts(jobVo.getFailedAttempts());
            }
            if (jobVo.getFailedInterval() != null) {
                builder.setFailedInterval(jobVo.getFailedInterval());
            }

            // 发送请求到server
            ServerModifyJobResponse response = (ServerModifyJobResponse) callActor(AkkaType.SERVER, builder.build());

            // 判断是否成功
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("edit job error", e);
            return errorResult(e.getMessage());
        }
    }

    /**
     * 修改job任务依赖
     *
     * @throws Exception
     */
    @POST
    @Path("dependency/set")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult dependencySet(@FormParam("appName") String appName,
                                    @FormParam("appToken") String appToken,
                                    @FormParam("user") String user,
                                    @FormParam("parameters") String parameters) {

        LOGGER.debug("更新job计划表达式");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JobDependencyVo jobVo = JsonHelper.fromJson(parameters, JobDependencyVo.class);
            // 构造请求
            RestModifyJobDependRequest.Builder builder = RestModifyJobDependRequest.newBuilder()
                    .setAppAuth(appAuth).setUser(user).setJobId(jobVo.getJobId());

            Preconditions.checkArgument(jobVo.getDependencyList() != null && jobVo.getDependencyList().size() > 0, "任务依赖为空");

            for (JobDependencyVo.DependencyEntry entryInput : jobVo.getDependencyList()) {
                builder.addDependencyEntry(ConvertValidUtils.ConvertDependencyEntry(entryInput));
            }

            // 发送请求到server
            ServerModifyJobDependResponse response = (ServerModifyJobDependResponse) callActor(AkkaType.SERVER, builder.build());

            // 判断是否成功
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("edit job error", e);
            return errorResult(e.getMessage());
        }
    }


    /**
     * 修改job计划表达式
     *
     * @throws Exception
     */
    @POST
    @Path("scheduleExp/set")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult scheduleExpSet(@FormParam("appName") String appName,
                                     @FormParam("appToken") String appToken,
                                     @FormParam("user") String user,
                                     @FormParam("parameters") String parameters) {

        LOGGER.debug("更新job依赖");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JobScheduleExpVo jobVo = JsonHelper.fromJson(parameters, JobScheduleExpVo.class);
            // 构造请求
            RestModifyJobScheduleExpRequest.Builder builder = RestModifyJobScheduleExpRequest.newBuilder().setAppAuth(appAuth).setUser(user).setJobId(jobVo.getJobId());

            Preconditions.checkArgument(jobVo.getScheduleExpressionList() != null && jobVo.getScheduleExpressionList().size() > 0, "计划表达式为空");

            if (jobVo.getScheduleExpressionList() != null && jobVo.getScheduleExpressionList().size() > 0) {
                for (JobScheduleExpVo.ScheduleExpressionEntry entryInput : jobVo.getScheduleExpressionList()) {
                    builder.addExpressionEntry(ConvertValidUtils.ConvertScheduleExpressionEntry(entryInput));
                }
            }

            // 发送请求到server
            ServerModifyJobScheduleExpResponse response = (ServerModifyJobScheduleExpResponse) callActor(AkkaType.SERVER, builder.build());

            // 判断是否成功
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("edit job error", e);
            return errorResult(e.getMessage());
        }
    }

    /**
     * 修改job任务状态
     *
     * @throws Exception
     */
    @POST
    @Path("status/set")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult statusSet(@FormParam("appName") String appName, @FormParam("appToken") String appToken, @FormParam("user") String user,
                                @FormParam("parameters") String parameters) {

        LOGGER.debug("更新job任务标志");
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters para = new JsonParameters(parameters);
            long jobId = para.getLongNotNull("jobId");
            int status = para.getIntegerNotNull("status");

            // 构造请求
            RestModifyJobStatusRequest.Builder builder = RestModifyJobStatusRequest.newBuilder().setAppAuth(appAuth).setUser(user).setJobId(jobId)
                    .setStatus(status);

            // 发送请求到server
            ServerModifyJobStatusResponse response = (ServerModifyJobStatusResponse) callActor(AkkaType.SERVER, builder.build());

            // 判断是否成功
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("edit job error", e);
            return errorResult(e.getMessage());
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
            Long jobId = paras.getLongNotNull("jobId");
            Integer relationType = paras.getIntegerNotNull("relationType");
            if (!JobRelationType.isValid(relationType)) {
                throw new IllegalArgumentException("参数不对。key='relationType',value=" + relationType.toString());
            }

            RestQueryJobRelationRequest request = RestQueryJobRelationRequest.newBuilder().setAppAuth(appAuth).setUser(user).setJobId(jobId)
                    .setRelationType(relationType).build();

            ServerQueryJobRelationResponse response = (ServerQueryJobRelationResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                JobRelationsVo vo = new JobRelationsVo();
                if (response.getJobStatusEntryOrBuilderList() != null) {
                    List<JobRelationsVo.JobStatusEntry> list = new ArrayList<>();
                    for (JobStatusEntry entry : response.getJobStatusEntryList()) {
                        list.add(new JobRelationsVo.JobStatusEntry().setJobId(entry.getJobId()).setStatus(entry.getStatus()));
                    }
                    vo.setList(list);
                }
                return successResult(vo);
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return errorResult(e.getMessage());
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