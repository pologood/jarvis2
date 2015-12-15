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

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.mogujie.jarvis.core.AbstractLogCollector;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.ProgressReporter;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.TaskDetail.TaskDetailBuilder;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.exeception.AcceptanceException;
import com.mogujie.jarvis.core.exeception.TaskException;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.HeartBeatProtos.HeartBeatResponse;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.WorkerKillTaskResponse;
import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.ServerRegistryResponse;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.WorkerRegistryRequest;
import com.mogujie.jarvis.protocol.ReportTaskStatusProtos.WorkerReportTaskStatusRequest;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.ServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.WorkerSubmitTaskResponse;
import com.mogujie.jarvis.worker.AbstractTask;
import com.mogujie.jarvis.worker.DefaultLogCollector;
import com.mogujie.jarvis.worker.DefaultProgressReporter;
import com.mogujie.jarvis.worker.TaskCallable;
import com.mogujie.jarvis.worker.TaskContext;
import com.mogujie.jarvis.worker.TaskContext.TaskContextBuilder;
import com.mogujie.jarvis.worker.TaskPool;
import com.mogujie.jarvis.worker.WorkerConfigKeys;
import com.mogujie.jarvis.worker.strategy.AcceptanceResult;
import com.mogujie.jarvis.worker.strategy.AcceptanceStrategy;
import com.mogujie.jarvis.worker.util.FutureUtils;
import com.mogujie.jarvis.worker.util.TaskConfigUtils;

import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class WorkerActor extends UntypedActor {

  private TaskPool taskPool = TaskPool.INSTANCE;

  private static ExecutorService executorService = Executors.newCachedThreadPool();

  private static final String SERVER_AKKA_PATH = ConfigUtils.getWorkerConfig()
      .getString(WorkerConfigKeys.SERVER_AKKA_PATH) + JarvisConstants.SERVER_AKKA_USER_PATH;

  private static final String LOGSERVER_AKKA_PATH = ConfigUtils.getWorkerConfig()
      .getString(WorkerConfigKeys.LOGSERVER_AKKA_PATH) + JarvisConstants.LOGSTORAGE_AKKA_USER_PATH;

  private static final Logger LOGGER = LogManager.getLogger();

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
    } else if (obj instanceof HeartBeatResponse) {
      HeartBeatResponse response = (HeartBeatResponse) obj;
      if (!response.getSuccess()) {
        registerWorker();
      }
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
    taskBuilder.setSchedulingTime(new DateTime(request.getSchedulingTime()));

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

    Pair<Class<? extends AbstractTask>, List<AcceptanceStrategy>> t2 = TaskConfigUtils
        .getRegisteredJobs().get(taskType);
    List<AcceptanceStrategy> strategies = t2.getSecond();
    for (AcceptanceStrategy strategy : strategies) {
      try {
        AcceptanceResult result = strategy.accept();
        if (!result.isAccepted()) {
          getSender().tell(WorkerSubmitTaskResponse.newBuilder().setAccept(false).setSuccess(true)
              .setMessage(result.getMessage()).build(), getSelf());
          return;
        }
      } catch (AcceptanceException e) {
        getSender().tell(WorkerSubmitTaskResponse.newBuilder().setAccept(false).setSuccess(false)
            .setMessage(e.getMessage()).build(), getSelf());
        return;
      }
    }

    getSender().tell(WorkerSubmitTaskResponse.newBuilder().setAccept(true).setSuccess(true).build(),
        getSelf());
    try {
      Constructor<? extends AbstractTask> constructor = t2.getFirst()
          .getConstructor(TaskContext.class);
      AbstractTask job = constructor.newInstance(contextBuilder.build());
      taskPool.add(fullId, job);
      serverActor.tell(WorkerReportTaskStatusRequest.newBuilder().setFullId(fullId)
          .setStatus(TaskStatus.RUNNING.getValue()).setTimestamp(System.currentTimeMillis() / 1000)
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
        serverActor.tell(WorkerReportTaskStatusRequest.newBuilder().setFullId(fullId)
            .setStatus(TaskStatus.SUCCESS.getValue())
            .setTimestamp(System.currentTimeMillis() / 1000).build(), getSelf());
      } else {
        serverActor.tell(WorkerReportTaskStatusRequest.newBuilder().setFullId(fullId)
            .setStatus(TaskStatus.FAILED.getValue()).setTimestamp(System.currentTimeMillis() / 1000)
            .build(), getSelf());
      }

      reporter.report(1);
      logCollector.collectStderr("", true);
      logCollector.collectStdout("", true);

      taskPool.remove(fullId);
    } catch (NoSuchMethodException | SecurityException | InstantiationException
        | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      getSender().tell(
          WorkerSubmitTaskResponse.newBuilder().setAccept(false).setMessage(e.getMessage()).build(),
          getSelf());
    }
  }

  private WorkerKillTaskResponse killJob(ServerKillTaskRequest request) {
    String fullId = request.getFullId();
    AbstractTask job = taskPool.get(fullId);
    if (job != null) {
      taskPool.remove(fullId);
      try {
        return WorkerKillTaskResponse.newBuilder().setSuccess(job.kill()).build();
      } catch (TaskException e) {
        return WorkerKillTaskResponse.newBuilder().setSuccess(false).setMessage(e.getMessage())
            .build();
      }
    }

    return WorkerKillTaskResponse.newBuilder().setSuccess(true).build();
  }

  private void registerWorker() {
    Configuration workerConfig = ConfigUtils.getWorkerConfig();
    String serverAkkaPath = workerConfig.getString(WorkerConfigKeys.SERVER_AKKA_PATH)
        + JarvisConstants.SERVER_AKKA_USER_PATH;
    int workerGroupId = workerConfig.getInt(WorkerConfigKeys.WORKER_GROUP_ID, 0);
    String workerKey = workerConfig.getString(WorkerConfigKeys.WORKER_KEY);
    WorkerRegistryRequest request = WorkerRegistryRequest.newBuilder().setKey(workerKey).build();

    // 注册Worker
    ActorSelection serverActor = getContext().actorSelection(serverAkkaPath);
    try {
      ServerRegistryResponse response = (ServerRegistryResponse) FutureUtils
          .awaitResult(serverActor, request, 30);
      if (!response.getSuccess()) {
        LOGGER.error("Worker register failed with group.id={}, worker.key={}", workerGroupId,
            workerKey);
        return;
      }
    } catch (Exception e) {
      LOGGER.error("Worker register failed", e);
      return;
    }
  }

}
