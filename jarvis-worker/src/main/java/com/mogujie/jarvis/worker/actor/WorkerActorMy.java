/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月12日 下午2:49:12
 */

package com.mogujie.jarvis.worker.actor;

import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.mogujie.jarvis.core.Constants;
import com.mogujie.sentinel.client.ActorLogCollector;

import com.mogujie.jarvis.core.JobContext;
import com.mogujie.jarvis.worker.JobPoolMy;

import com.mogujie.sentinel.client.LogCollector;
import com.mogujie.sentinel.client.job.HookJob;
import com.mogujie.sentinel.client.job.Job;
import com.mogujie.sentinel.client.util.ClientConstants;
import com.mogujie.sentinel.core.common.SentinelConstants;
import com.mogujie.sentinel.core.domain.JobStatus;
import com.mogujie.jarvis.core.common.util.ConfigUtils;

import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitJobRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.WorkerKillTaskResponse;


/**
 * @author wuya
 *
 */
public class WorkerActorMy extends UntypedActor {

  private static JobPoolMy jobPool = JobPoolMy.getInstance();

  public static Props props() {
    return Props.create(WorkerActorMy.class);
  }


    @Override
  public void onReceive(Object obj) throws Exception {
    if (obj instanceof ServerSubmitJobRequest) {
        ServerSubmitJobRequest request = (ServerSubmitJobRequest) obj;
      submitJob(request);
    } else if (obj instanceof ServerKillTaskRequest) {
      killJob((ServerKillTaskRequest) obj);
      getSender().tell(WorkerKillTaskResponse.newBuilder().setSuccess(true).build(), getSelf());
    } else {
      unhandled(obj);
    }
  }


  private void submitJob(ServerSubmitJobRequest msg) {
    String jobStatusPath = ConfigUtils.getClientConfig().getString("server.akka.path") + "/user/"
        + Constants.SERVER_JOB_STATUS_AKKA_PATH;
    ActorSelection jobStatusActor = getContext().actorSelection(jobStatusPath);

      String jobId = msg.getFullId();
    JobContext context = JobContext.newBuilder()
            .setFullId(msg.getFullId())
            .setJobName(msg.getJobName())
            .setAppName(msg.getAppName())
            .



            required string full_id = 1;
      required string job_name = 2;
      required string app_name = 3;
      required string user = 4;
      required string job_type = 5;
      required string command = 6;
      optional int32 priority = 7 [default = 1];
      repeated MapEntry parameters = 8;


    context.seId(jobId);
    context.setAppName(msg.getAppName());
    context.setJobName(msg.getJobName());
    context.setJobType(msg.getJobType());
    context.setUser(msg.getUser());
    context.setCommand(msg.getCommand());


    LogCollector logCollector = new ActorLogCollector(getContext().system(), msg.getJobId());
    context.setLogCollector(logCollector);

    // 1.提交job，进行预处理
    String jobType = msg.getJobType();
    Job job = null;
    try {
      job = ClientConstants.JOBS.get(jobType)._1.newInstance();
      job.setJobContext(context);
      if (job instanceof HookJob) {
        // 如果失败，将抛JobHookException
        ((HookJob)job).preExecute();
      }
    } catch (Exception e) {
      logCollector.collectStderr(e.getMessage(), true);
      // preExecute执行失败，即提交失败，SubmitJobResponse设为拒绝
      getSender().tell(SubmitJobResponse.newBuilder().setAccept(false)
          .setMessage(e.getMessage()).build(), getSelf());
    }
    // preExecute执行完成，即提交成功，SubmitJobResponse设为可接收
    getSender().tell(SubmitJobResponse.newBuilder().setAccept(true).build(), getSelf());

    // 2. 执行job
    StatusMessage statusMessage = StatusMessage.newBuilder().setJobId(jobId)
        .setStatus(JobStatus.RUNNING.getValue()).build();
    jobStatusActor.tell(statusMessage, getSelf());
    jobPool.add(jobId, job);
    if (job.execute()) {
      jobStatusActor.tell(
          StatusMessage.newBuilder(statusMessage).setStatus(JobStatus.SUCCESS.getValue()).build(),
          getSelf());
    } else {
      jobStatusActor.tell(
          StatusMessage.newBuilder(statusMessage).setStatus(JobStatus.FAILED.getValue()).build(),
          getSelf());
    }

    logCollector.collectStderr("", true);
    logCollector.collectStdout("", true);
    jobPool.remove(jobId);
  }

  private void killJob(ServerKillTaskRequest msg) {
    long jobId = msg.getJobId();
    Job job = jobPool.get(jobId);
    if (job != null) {
      jobPool.remove(jobId);
      job.kill();
    }
  }

}
