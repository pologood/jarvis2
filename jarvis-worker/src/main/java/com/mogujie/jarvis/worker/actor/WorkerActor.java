/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月7日 下午1:20:15
 */

package com.mogujie.jarvis.worker.actor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.mogujie.jarvis.core.AbstractLogCollector;
import com.mogujie.jarvis.core.DefaultLogCollector;
import com.mogujie.jarvis.core.DefaultProgressReporter;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.ProgressReporter;
import com.mogujie.jarvis.core.TaskContext;
import com.mogujie.jarvis.core.TaskContext.TaskContextBuilder;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.TaskDetail.TaskDetailBuilder;
import com.mogujie.jarvis.core.exeception.AcceptionException;
import com.mogujie.jarvis.core.exeception.TaskException;
import com.mogujie.jarvis.core.task.AbstractTask;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.WorkerKillTaskResponse;
import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;
import com.mogujie.jarvis.protocol.ReportStatusProtos.WorkerReportStatusRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.WorkerSubmitTaskResponse;
import com.mogujie.jarvis.worker.TaskCallable;
import com.mogujie.jarvis.worker.TaskPool;
import com.mogujie.jarvis.worker.strategy.AcceptionResult;
import com.mogujie.jarvis.worker.strategy.AcceptionStrategy;
import com.mogujie.jarvis.worker.util.TaskConfigUtils;

import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.Tuple2;

public class WorkerActor extends UntypedActor {

  private static ExecutorService executorService = Executors.newCachedThreadPool();
  private static TaskPool jobPool = TaskPool.getInstance();
  private static final String SERVER_AKKA_PATH = ConfigUtils.getWorkerConfig()
      .getString("server.akka.path") + "/user/" + JarvisConstants.SERVER_AKKA_SYSTEM_NAME;
  private static final String LOGSERVER_AKKA_PATH = ConfigUtils.getWorkerConfig()
      .getString("logserver.akka.path") + "/user/" + JarvisConstants.LOGSERVER_AKKA_SYSTEM_NAME;

  public static Props props() {
    return Props.create(WorkerActor.class);
  }

  @Override
  public void onReceive(Object obj) throws Exception {
    if (obj instanceof ServerSubmitTaskRequest) {
      ServerSubmitTaskRequest request = (ServerSubmitTaskRequest) obj;
      submitJob(request);
    } else if (obj instanceof ServerKillTaskRequest) {
      ServerKillTaskRequest request = (ServerKillTaskRequest) obj;
      WorkerKillTaskResponse response = killJob(request);
      getSender().tell(response, getSelf());
    } else {
      unhandled(obj);
    }
  }

  private void submitJob(ServerSubmitTaskRequest request) {
    String fullId = request.getFullId();
    String taskType = request.getTaskType();
    TaskDetailBuilder taskBuilder = TaskDetail.newTaskDetailBuilder();
    taskBuilder.setFullId(fullId);
    taskBuilder.setTaskName(request.getTaskName());
    taskBuilder.setAppName(request.getAppName());
    taskBuilder.setUser(request.getUser());
    taskBuilder.setTaskType(taskType);
    taskBuilder.setContent(request.getContent());
    taskBuilder.setPriority(request.getPriority());

    Map<String, Object> map = new HashMap<>();
    List<MapEntry> parameters = request.getParametersList();
    for (int i = 0, len = parameters.size(); i < len; i++) {
      MapEntry entry = parameters.get(i);
      map.put(entry.getKey(), entry.getValue());
    }
    taskBuilder.setParameters(map);

    TaskContextBuilder contextBuilder = TaskContext.newBuilder();
    contextBuilder.setTaskDetail(taskBuilder.build());

    ActorSelection logActor = getContext().actorSelection(LOGSERVER_AKKA_PATH);
    AbstractLogCollector logCollector = new DefaultLogCollector(logActor, fullId);
    contextBuilder.setLogCollector(logCollector);

    ActorSelection serverActor = getContext().actorSelection(SERVER_AKKA_PATH);
    ProgressReporter reporter = new DefaultProgressReporter(serverActor, fullId);
    contextBuilder.setProgressReporter(reporter);

    Tuple2<Class<? extends AbstractTask>, List<AcceptionStrategy>> t2 = TaskConfigUtils
        .getRegisteredJobs().get(taskType);
    List<AcceptionStrategy> strategies = t2._2;
    for (AcceptionStrategy strategy : strategies) {
      try {
        AcceptionResult result = strategy.accept();
        if (!result.isAccepted()) {
          getSender().tell(WorkerSubmitTaskResponse.newBuilder().setAccept(false)
              .setMessage(result.getMessage()).build(), getSelf());
          return;
        }
      } catch (AcceptionException e) {
        getSender().tell(WorkerSubmitTaskResponse.newBuilder().setAccept(false)
            .setMessage(e.getMessage()).build(), getSelf());
        return;
      }
    }

    getSender().tell(WorkerSubmitTaskResponse.newBuilder().setAccept(true).build(), getSelf());
    try {
      Constructor<? extends AbstractTask> constructor = t2._1.getConstructor(TaskContext.class);
      AbstractTask job = constructor.newInstance(contextBuilder.build());
      jobPool.add(fullId, job);
      serverActor.tell(WorkerReportStatusRequest.newBuilder().setFullId(fullId)
          .setStatus(JobStatus.RUNNING.getValue()).setTimestamp(System.currentTimeMillis() / 1000)
          .build(), getSelf());
      reporter.report(0);
      Callable<Boolean> task = new TaskCallable(job);
      Future<Boolean> future = executorService.submit(task);
      boolean result = false;
      try {
        result = future.get();
      } catch (InterruptedException | ExecutionException e) {
        logCollector.collectStderr(e.getMessage(), true);
      }

      if (result) {
        serverActor.tell(WorkerReportStatusRequest.newBuilder().setFullId(fullId)
            .setStatus(JobStatus.SUCCESS.getValue()).setTimestamp(System.currentTimeMillis() / 1000)
            .build(), getSelf());
      } else {
        serverActor.tell(WorkerReportStatusRequest.newBuilder().setFullId(fullId)
            .setStatus(JobStatus.FAILED.getValue()).setTimestamp(System.currentTimeMillis() / 1000)
            .build(), getSelf());
      }

      reporter.report(1);
      logCollector.collectStderr("", true);
      logCollector.collectStdout("", true);

      jobPool.remove(fullId);
    } catch (NoSuchMethodException | SecurityException | InstantiationException
        | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      getSender().tell(
          WorkerSubmitTaskResponse.newBuilder().setAccept(false).setMessage(e.getMessage()).build(),
          getSelf());
    }
  }

  private WorkerKillTaskResponse killJob(ServerKillTaskRequest request) {
    String fullId = request.getFullId();
    AbstractTask job = jobPool.get(fullId);
    if (job != null) {
      jobPool.remove(fullId);
      try {
        return WorkerKillTaskResponse.newBuilder().setSuccess(job.kill()).build();
      } catch (TaskException e) {
        return WorkerKillTaskResponse.newBuilder().setSuccess(false).setMessage(e.getMessage())
            .build();
      }
    }

    return WorkerKillTaskResponse.newBuilder().setSuccess(true).build();
  }

}
