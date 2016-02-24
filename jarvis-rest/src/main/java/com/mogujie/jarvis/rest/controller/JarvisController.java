/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月23日 下午1:17:37
 */

package com.mogujie.jarvis.rest.controller;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.domain.AlarmType;
import com.mogujie.jarvis.core.domain.CommonStrategy;
import com.mogujie.jarvis.core.domain.JobContentType;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.expression.ScheduleExpressionType;
import com.mogujie.jarvis.core.util.AppTokenUtils;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.protocol.AlarmProtos;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.JobInfoEntryProtos.JobInfoEntry;
import com.mogujie.jarvis.protocol.JobProtos;
import com.mogujie.jarvis.protocol.SearchJobProtos.RestSearchAllJobsRequest;
import com.mogujie.jarvis.protocol.SearchJobProtos.RestSearchJobByScriptIdRequest;
import com.mogujie.jarvis.protocol.SearchJobProtos.RestSearchPreJobInfoRequest;
import com.mogujie.jarvis.protocol.SearchJobProtos.ServerSearchAllJobsResponse;
import com.mogujie.jarvis.protocol.SearchJobProtos.ServerSearchJobByScriptIdResponse;
import com.mogujie.jarvis.protocol.SearchJobProtos.ServerSearchPreJobInfoResponse;
import com.mogujie.jarvis.rest.jarvis.Result;
import com.mogujie.jarvis.rest.jarvis.TaskInfo;
import com.mogujie.jarvis.rest.jarvis.TaskInfoResult;
import com.mogujie.jarvis.rest.jarvis.TasksResult;
import com.mogujie.jarvis.rest.jarvis.User;
import com.mogujie.jarvis.rest.utils.ValidUtils;
import com.mogujie.jarvis.rest.vo.JobDependencyVo;
import com.mogujie.jarvis.rest.vo.JobScheduleExpVo;
import com.mogujie.jarvis.rest.vo.JobVo;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.joda.time.DateTime;

/**
 * 兼容旧jarvis rest接口
 *
 * @author guangming
 *
 */
@Deprecated
@Path("api")
public class JarvisController extends AbstractController {

    private static String APP_IRONMAN_NAME = ConfigUtils.getRestConfig().getString("app.ironman.name");
    private static String APP_IRONMAN_KEY = ConfigUtils.getRestConfig().getString("app.ironman.key");
    private static String APP_XMEN_NAME = ConfigUtils.getRestConfig().getString("app.xmen.name");
    private static String APP_XMEN_KEY = ConfigUtils.getRestConfig().getString("app.xmen.key");

    @GET
    @Path("taskinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public TaskInfoResult getTaskInfo(@PathParam("scriptId") int scriptId) {
        LOGGER.debug("根据scriptId查询taskinfo");
        try {
            String appToken = AppTokenUtils.generateToken(DateTime.now().getMillis(), APP_IRONMAN_KEY);
            AppAuth appAuth = AppAuth.newBuilder().setName(APP_IRONMAN_NAME).setToken(appToken).build();

            RestSearchJobByScriptIdRequest request = RestSearchJobByScriptIdRequest.newBuilder().setAppAuth(appAuth)
                    .setUser(APP_IRONMAN_NAME).setScriptId(scriptId).build();

            ServerSearchJobByScriptIdResponse response = (ServerSearchJobByScriptIdResponse) callActor(AkkaType.SERVER, request);

            if (response.getSuccess()) {
                JobInfoEntry jobInfo = response.getJobInfo();
                TaskInfoResult result = new TaskInfoResult(jobInfo);
                result.setSuccess(true);
                RestSearchPreJobInfoRequest searchPreJobInfoRequest = RestSearchPreJobInfoRequest.newBuilder()
                        .setAppAuth(appAuth).setUser(APP_IRONMAN_NAME).setJobId(jobInfo.getJobId()).build();
                ServerSearchPreJobInfoResponse searchPreJobInfoResponse = (ServerSearchPreJobInfoResponse) callActor(AkkaType.SERVER, searchPreJobInfoRequest);
                if (searchPreJobInfoResponse.getSuccess()) {
                    List<JobInfoEntry> preJobInfos = searchPreJobInfoResponse.getPreJobInfoList();
                    result.setPreJobInfos(preJobInfos);
                } else {
                    LOGGER.error("获取jobId={}的依赖关系失败", jobInfo.getJobId());
                }
                return result;
            } else {
                TaskInfoResult result = new TaskInfoResult();
                result.setSuccess(false);
                result.setMessage(response.getMessage());
                return result;
            }
        } catch (Exception e) {
            TaskInfoResult result = new TaskInfoResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    @GET
    @Path("alltasks")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult getAllTasks() {
        LOGGER.debug("查询所有jobs");
        try {
            String appToken = AppTokenUtils.generateToken(DateTime.now().getMillis(), APP_IRONMAN_KEY);
            AppAuth appAuth = AppAuth.newBuilder().setName(APP_IRONMAN_NAME).setToken(appToken).build();

            RestSearchAllJobsRequest request = RestSearchAllJobsRequest.newBuilder().setAppAuth(appAuth)
                    .setUser(APP_IRONMAN_NAME).build();
            ServerSearchAllJobsResponse response = (ServerSearchAllJobsResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                List<JobInfoEntry> jobInfos = response.getJobInfoList();
                TasksResult result = new TasksResult(jobInfos);
                result.setSuccess(true);
                return result;
            } else {
                TasksResult result = new TasksResult();
                result.setSuccess(false);
                result.setMessage(response.getMessage());
                return result;
            }
        } catch (Exception e) {
            TasksResult result = new TasksResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    @GET
    @Path("getdependencybyscript.htm")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult getDependencyByScript(@PathParam("scriptId") int scriptId) {
        LOGGER.debug("根据scriptId查询依赖关系");
        try {
            String appToken = AppTokenUtils.generateToken(DateTime.now().getMillis(), APP_IRONMAN_KEY);
            AppAuth appAuth = AppAuth.newBuilder().setName(APP_IRONMAN_NAME).setToken(appToken).build();

            RestSearchJobByScriptIdRequest request = RestSearchJobByScriptIdRequest.newBuilder().setAppAuth(appAuth)
                    .setUser(APP_IRONMAN_NAME).setScriptId(scriptId).build();

            ServerSearchJobByScriptIdResponse response = (ServerSearchJobByScriptIdResponse) callActor(AkkaType.SERVER, request);

            if (response.getSuccess()) {
                JobInfoEntry jobInfo = response.getJobInfo();
                RestSearchPreJobInfoRequest searchPreJobInfoRequest = RestSearchPreJobInfoRequest.newBuilder()
                        .setAppAuth(appAuth).setUser(APP_IRONMAN_NAME).setJobId(jobInfo.getJobId()).build();
                ServerSearchPreJobInfoResponse searchPreJobInfoResponse = (ServerSearchPreJobInfoResponse) callActor(AkkaType.SERVER, searchPreJobInfoRequest);
                if (searchPreJobInfoResponse.getSuccess()) {
                    List<JobInfoEntry> preJobInfos = searchPreJobInfoResponse.getPreJobInfoList();
                    TasksResult result = new TasksResult(preJobInfos);
                    result.setSuccess(true);
                    return result;
                } else {
                    LOGGER.error("获取jobId={}的依赖关系失败", jobInfo.getJobId());
                    TasksResult result = new TasksResult();
                    result.setSuccess(false);
                    result.setMessage("获取jobId=" + jobInfo.getJobId() + "的依赖关系失败");
                    return result;
                }
            } else {
                TasksResult result = new TasksResult();
                result.setSuccess(false);
                result.setMessage("通过srciptId=" + scriptId + "查找job失败");
                return result;
            }
        } catch (Exception e) {
            TasksResult result = new TasksResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    @GET
    @Path("sdependtasks")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult getScriptDepend(@PathParam("scriptId") int scriptId) {
        return getDependencyByScript(scriptId);
    }

    @GET
    @Path("searchtask")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult searchTask(@PathParam("keyword") String title) {
        // NOT SUPPORTED
        // 暂时没人用到
        return null;
    }

    @POST
    @Path("submittask")
    @Produces(MediaType.APPLICATION_JSON)
    public Result submitTask(@FormParam("task") String task, @FormParam("globalUser") String globalUser,
            User user) {
        TaskInfo taskInfo = JsonHelper.fromJson(task, TaskInfo.class);
        return null;
    }

  @POST
  @Path("addtaskwithdependency")
  @Produces(MediaType.APPLICATION_JSON)
  public TasksResult addTask(@FormParam("scriptId") Integer scriptId, @FormParam("taskTitle") String taskTitle,
      @FormParam("beforeTaskId") String beforeTaskId, @FormParam("cronExp") String cronExp,
      @FormParam("priority") Integer priority, @FormParam("creator") String creator,
      @FormParam("receiver") String receiver, @FormParam("scode") String scode, @FormParam("token") String token) {

    LOGGER.debug("add job dependency relation");

    try {
      String appToken = AppTokenUtils.generateToken(DateTime.now().getMillis(), APP_XMEN_KEY);
      AppAuth appAuth = AppAuth.newBuilder().setName(APP_XMEN_NAME).setToken(appToken).build();

      JobVo job = new JobVo();
      job.setAppName(APP_XMEN_NAME);
      job.setJobType("shell");
      job.setContentType(JobContentType.SCRIPT.getValue());
      job.setStatus(JobStatus.ENABLE.getValue());
      job.setJobName(taskTitle);
      job.setContent(String.valueOf(scriptId));
      job.setWorkerGroupId(1);

      // 计划表达式
      List<JobScheduleExpVo.ScheduleExpressionEntry> list = new ArrayList<>();
      JobScheduleExpVo.ScheduleExpressionEntry expressionEntry;

      expressionEntry = new JobScheduleExpVo.ScheduleExpressionEntry();
      expressionEntry.setOperatorMode(OperationMode.ADD.getValue());
      expressionEntry.setExpressionType(ScheduleExpressionType.CRON.getValue());
      expressionEntry.setExpression(cronExp);
      list.add(expressionEntry);

      job.setScheduleExpressionList(list);

      // 依赖任务
      List<JobDependencyVo.DependencyEntry> dependList = new ArrayList<>();
      JobDependencyVo.DependencyEntry dependencyEntry;

      dependencyEntry = new JobDependencyVo.DependencyEntry();
      dependencyEntry.setOperatorMode(OperationMode.ADD.getValue());
      dependencyEntry.setPreJobId(Long.valueOf(beforeTaskId));
      dependencyEntry.setCommonStrategy(CommonStrategy.ALL.getValue());
      dependList.add(dependencyEntry);

      job.setDependencyList(dependList);

      ValidUtils.checkJob(ValidUtils.CheckMode.ADD, job);
      JobProtos.RestSubmitJobRequest request = JobController.vo2RequestByAdd(job, appAuth, creator);

      // 发送请求到server
      JobProtos.ServerSubmitJobResponse response =
          (JobProtos.ServerSubmitJobResponse) callActor(AkkaType.SERVER, request);
      TasksResult result = new TasksResult();
      result.setMessage(response.getMessage());
      if (response.getSuccess()) {
        // add job alarm
        AlarmProtos.RestCreateAlarmRequest alarmRequest = AlarmProtos.RestCreateAlarmRequest.newBuilder()
            .setAppAuth(appAuth)
            .setUser(creator)
            .setJobId(response.getJobId())
            .setAlarmType(String.valueOf(AlarmType.SMS.getValue()))
            .setReciever(receiver)
            .setStatus(JobStatus.ENABLE.getValue())
            .build();
        AlarmProtos.ServerCreateAlarmResponse
            alarmResponse = (AlarmProtos.ServerCreateAlarmResponse) callActor(AkkaType.SERVER, alarmRequest);
        LOGGER.info(alarmResponse.getMessage());
        // TODO add job rollback if alarm add failure
        result.setSuccess(true);
      } else {
        result.setSuccess(false);
      }
      return result;
    } catch (Exception e) {
      TasksResult result = new TasksResult();
      result.setSuccess(false);
      result.setMessage(e.getMessage());
      return result;
    }
  }

    @Path("updateTaskRelation")
    public Result updateTaskRelation(@FormParam("oldPreScriptList") List<String> oldPreScriptList,
        @FormParam("newPreScriptList") List<String> newPreScriptList, @FormParam("scriptName") String scriptName) {
        return null;
    }

}
