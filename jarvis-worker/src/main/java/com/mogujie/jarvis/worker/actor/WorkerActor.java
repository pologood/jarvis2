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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.google.common.collect.Queues;
import com.mogujie.jarvis.core.AbstractLogCollector;
import com.mogujie.jarvis.core.AbstractTask;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.ProgressReporter;
import com.mogujie.jarvis.core.TaskContext;
import com.mogujie.jarvis.core.TaskContext.TaskContextBuilder;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.TaskDetail.TaskDetailBuilder;
import com.mogujie.jarvis.core.exception.TaskException;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.HeartBeatProtos.HeartBeatResponse;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.WorkerKillTaskResponse;
import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.ServerRegistryResponse;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.WorkerRegistryRequest;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.ServerSubmitTaskRequest;
import com.mogujie.jarvis.worker.DefaultLogCollector;
import com.mogujie.jarvis.worker.DefaultProgressReporter;
import com.mogujie.jarvis.worker.TaskExecutor;
import com.mogujie.jarvis.worker.TaskPool;
import com.mogujie.jarvis.worker.WorkerConfigKeys;
import com.mogujie.jarvis.worker.util.FutureUtils;

public class WorkerActor extends UntypedActor {

    private TaskPool taskPool = TaskPool.INSTANCE;

    private static Configuration workerConfig = ConfigUtils.getWorkerConfig();
    private static int corePoolSize = workerConfig.getInt(WorkerConfigKeys.WORKER_EXECUTOR_POOL_CORE_SIZE, 5);
    private static int maximumPoolSize = workerConfig.getInt(WorkerConfigKeys.WORKER_EXECUTOR_POOL_MAXIMUM_SIZE, 20);
    private static int keepAliveTime = workerConfig.getInt(WorkerConfigKeys.WORKER_EXECUTOR_POOL_KEEP_ALIVE_SECONDS, 3600);
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
            Queues.newArrayBlockingQueue(corePoolSize));

    private static final String SERVER_AKKA_PATH = ConfigUtils.getWorkerConfig().getString(WorkerConfigKeys.SERVER_AKKA_PATH)
            + JarvisConstants.SERVER_AKKA_USER_PATH;

    private static final String LOGSERVER_AKKA_PATH = ConfigUtils.getWorkerConfig().getString(WorkerConfigKeys.LOGSERVER_AKKA_PATH)
            + JarvisConstants.LOGSTORAGE_AKKA_USER_PATH;

    private static final Logger LOGGER = LogManager.getLogger();

    public static Props props() {
        return Props.create(WorkerActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof ServerSubmitTaskRequest) {
            ServerSubmitTaskRequest request = (ServerSubmitTaskRequest) obj;
            submitTask(request);
        } else if (obj instanceof ServerKillTaskRequest) {
            ServerKillTaskRequest request = (ServerKillTaskRequest) obj;
            WorkerKillTaskResponse response = killTask(request);
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

    private void submitTask(ServerSubmitTaskRequest request) {
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
        taskBuilder.setDataTime(new DateTime(request.getDataTime()));

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

        threadPoolExecutor.execute(new TaskExecutor(contextBuilder.build(), getSelf(), getSender(), serverActor));
    }

    private WorkerKillTaskResponse killTask(ServerKillTaskRequest request) {
        String fullId = request.getFullId();
        AbstractTask task = taskPool.get(fullId);
        if (task != null) {
            taskPool.remove(fullId);
            try {
                return WorkerKillTaskResponse.newBuilder().setSuccess(task.kill()).build();
            } catch (TaskException e) {
                return WorkerKillTaskResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            }
        }

        return WorkerKillTaskResponse.newBuilder().setSuccess(true).build();
    }

    private void registerWorker() {
        Configuration workerConfig = ConfigUtils.getWorkerConfig();
        String serverAkkaPath = workerConfig.getString(WorkerConfigKeys.SERVER_AKKA_PATH) + JarvisConstants.SERVER_AKKA_USER_PATH;
        int workerGroupId = workerConfig.getInt(WorkerConfigKeys.WORKER_GROUP_ID, 0);
        String workerKey = workerConfig.getString(WorkerConfigKeys.WORKER_KEY);
        WorkerRegistryRequest request = WorkerRegistryRequest.newBuilder().setKey(workerKey).build();

        // 注册Worker
        ActorSelection serverActor = getContext().actorSelection(serverAkkaPath);
        try {
            ServerRegistryResponse response = (ServerRegistryResponse) FutureUtils.awaitResult(serverActor, request, 30);
            if (!response.getSuccess()) {
                LOGGER.error("Worker register failed with group.id={}, worker.key={}", workerGroupId, workerKey);
                return;
            }else{
                LOGGER.info("Worker register successful");
            }
        } catch (Exception e) {
            LOGGER.error("Worker register failed", e);
            return;
        }
    }

}