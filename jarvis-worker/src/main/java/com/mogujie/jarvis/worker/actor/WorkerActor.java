/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月7日 下午1:20:15
 */

package com.mogujie.jarvis.worker.actor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mogujie.jarvis.core.AbstractLogCollector;
import com.mogujie.jarvis.core.DefaultLogCollector;
import com.mogujie.jarvis.core.DefaultProgressReporter;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.JobContext;
import com.mogujie.jarvis.core.JobContext.JobContextBuilder;
import com.mogujie.jarvis.core.ProgressReporter;
import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.exeception.JobException;
import com.mogujie.jarvis.core.job.AbstractJob;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.WorkerKillTaskResponse;
import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.WorkerSubmitTaskResponse;
import com.mogujie.jarvis.worker.JobPool;

import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class WorkerActor extends UntypedActor {

  private static ExecutorService executorService = Executors.newCachedThreadPool();
  private static JobPool jobPool = JobPool.getInstance();
  private static final String SERVER_AKKA_PATH = ConfigUtils.getWorkerConfig()
      .getString("server.akka.path") + "/user/" + JarvisConstants.SERVER_AKKA_PATH;
  private static final String LOGSERVER_AKKA_PATH = ConfigUtils.getWorkerConfig()
      .getString("logserver.akka.path") + "/user/" + JarvisConstants.LOGSERVER_AKKA_PATH;

  public static Props props() {
    return Props.create(WorkerActor.class);
  }

  @Override
  public void onReceive(Object obj) throws Exception {
    if (obj instanceof ServerSubmitTaskRequest) {

    } else if (obj instanceof ServerKillTaskRequest) {
      ServerKillTaskRequest request = (ServerKillTaskRequest) obj;
      WorkerKillTaskResponse response = killJob(request);
      getSender().tell(response, getSelf());
    } else {
      unhandled(obj);
    }
  }

  private WorkerSubmitTaskResponse submitJob(ServerSubmitTaskRequest request) {
    String fullId = request.getFullId();

    JobContextBuilder contextBuilder = JobContext.newBuilder();
    contextBuilder.setFullId(fullId);
    contextBuilder.setJobName(request.getJobName());
    contextBuilder.setAppName(request.getAppName());
    contextBuilder.setUser(request.getUser());
    contextBuilder.setJobType(request.getJobType());
    contextBuilder.setCommand(request.getCommand());
    contextBuilder.setPriority(request.getPriority());

    Map<String, Object> map = new HashMap<>();
    List<MapEntry> parameters = request.getParametersList();
    for (int i = 0, len = parameters.size(); i < len; i++) {
      MapEntry entry = parameters.get(i);
      map.put(entry.getKey(), entry.getValue());
    }

    contextBuilder.setParameters(map);

    ActorSelection logActor = getContext().actorSelection(LOGSERVER_AKKA_PATH);
    AbstractLogCollector logCollector = new DefaultLogCollector(logActor, fullId);
    contextBuilder.setLogCollector(logCollector);

    ActorSelection reportActor = getContext().actorSelection(SERVER_AKKA_PATH);
    ProgressReporter reporter = new DefaultProgressReporter(reportActor, fullId);
    contextBuilder.setProgressReporter(reporter);

    return null;
  }

  private WorkerKillTaskResponse killJob(ServerKillTaskRequest request) {
    String fullId = request.getFullId();
    AbstractJob job = jobPool.get(fullId);
    if (job != null) {
      jobPool.remove(fullId);
      try {
        return WorkerKillTaskResponse.newBuilder().setSuccess(job.kill()).build();
      } catch (JobException e) {
        return WorkerKillTaskResponse.newBuilder().setSuccess(false).setMessage(e.getMessage())
            .build();
      }
    }

    return WorkerKillTaskResponse.newBuilder().setSuccess(true).build();
  }

}
