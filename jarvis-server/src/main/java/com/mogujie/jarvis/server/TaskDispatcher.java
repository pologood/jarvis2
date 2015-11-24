/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月25日 上午11:39:46
 */

package com.mogujie.jarvis.server;

import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.WorkerSubmitTaskResponse;
import com.mogujie.jarvis.server.scheduler.TaskRetryScheduler;
import com.mogujie.jarvis.server.service.AppService;
import com.mogujie.jarvis.server.util.FutureUtils;
import com.mogujie.jarvis.server.workerselector.WorkerSelector;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;

@Repository
@Scope("prototype")
public class TaskDispatcher extends Thread {

    private TaskQueue queue = TaskQueue.INSTANCE;
    private TaskRetryScheduler taskRetryScheduler = TaskRetryScheduler.INSTANCE;

    @Autowired
    private AppService appService;

    @Autowired
    private WorkerSelector workerSelector;

    @Autowired
    private TaskManager taskManager;

    private volatile boolean running = true;

    private ActorSystem system = JarvisServerActorSystem.getInstance();

    private static final Logger LOGGER = LogManager.getLogger();

    public void pause() {
        running = false;
    }

    public void restart() {
        running = true;
    }

    @Override
    public void run() {
        while (true) {
            if (running) {
                try {
                    TaskDetail task = queue.take();
                    String appName = task.getAppName();
                    int appId = appService.getAppIdByName(appName);

                    ServerSubmitTaskRequest.Builder builder = ServerSubmitTaskRequest.newBuilder();
                    builder = builder.setFullId(task.getFullId());
                    builder = builder.setTaskName(task.getTaskName());
                    builder = builder.setAppName(task.getAppName());
                    builder = builder.setUser(task.getUser());
                    builder = builder.setTaskType(task.getTaskType());
                    builder = builder.setContent(task.getContent());
                    builder = builder.setPriority(task.getPriority());

                    int i = 0;
                    for (Entry<String, Object> entry : task.getParameters().entrySet()) {
                        MapEntry mapEntry = MapEntry.newBuilder().setKey(entry.getKey()).setValue(entry.getValue().toString()).build();
                        builder.setParameters(i++, mapEntry);
                    }
                    ServerSubmitTaskRequest request = builder.build();

                    WorkerInfo workerInfo = workerSelector.select(task.getGroupId());
                    if (workerInfo != null) {
                        String fullId = task.getFullId();
                        boolean allowed = taskManager.addTask(fullId, workerInfo, appId);
                        if (allowed) {
                            ActorSelection actorSelection = system.actorSelection(workerInfo.getAkkaRootPath());
                            try {
                                WorkerSubmitTaskResponse response = (WorkerSubmitTaskResponse) FutureUtils.awaitResult(actorSelection, request, 30);
                                if (response.getSuccess()) {
                                    if (response.getAccept()) {
                                        LOGGER.debug("Task[{}] was accepted by worker[{}:{}]", fullId, workerInfo.getIp(), workerInfo.getPort());
                                        continue;
                                    } else {
                                        LOGGER.warn("Task[{}] was rejected by worker[{}:{}]", fullId, workerInfo.getIp(), workerInfo.getPort());
                                        taskRetryScheduler.addTask(task, task.getRejectRetries(), task.getRejectInterval());
                                    }
                                } else {
                                    LOGGER.error("Send ServerSubmitTaskRequest error: " + response.getMessage());
                                }
                            } catch (Exception e) {
                                LOGGER.error("Send ServerSubmitTaskRequest error", e);
                            }
                        } else {
                            LOGGER.warn("The running task number of App[{}] more than maximum parallelism", appName);
                        }
                        taskManager.appCounterDecrement(appId);
                    }

                    queue.put(task);
                } catch (InterruptedException e) {
                    LOGGER.error("Take taskDetail error from taskQueue", e);
                }
            } else {
                yield();
            }
        }
    }
}
