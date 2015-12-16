/*
 * 蘑菇街 Inc. Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年9月25日 上午11:39:46
 */

package com.mogujie.jarvis.server.dispatcher;

import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.ServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.WorkerSubmitTaskResponse;
import com.mogujie.jarvis.server.dispatcher.workerselector.WorkerSelector;
import com.mogujie.jarvis.server.domain.RetryType;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.TaskRetryScheduler;
import com.mogujie.jarvis.server.service.AppService;
import com.mogujie.jarvis.server.util.FutureUtils;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;

public class TaskDispatcher extends Thread {

    private TaskQueue queue = Injectors.getInjector().getInstance(TaskQueue.class);
    private TaskRetryScheduler taskRetryScheduler = TaskRetryScheduler.INSTANCE;

    private AppService appService = Injectors.getInjector().getInstance(AppService.class);

    private WorkerSelector workerSelector = Injectors.getInjector().getInstance(WorkerSelector.class);

    private TaskManager taskManager = Injectors.getInjector().getInstance(TaskManager.class);

    private volatile boolean running = true;

    private ActorSystem system = Injectors.getInjector().getInstance(ActorSystem.class);

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
                TaskDetail task = null;
                try {
                    task = queue.take();
                } catch (InterruptedException e) {
                    LOGGER.error("Take taskDetail error from taskQueue", e);
                }

                if (task == null) {
                    continue;
                }

                try {
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
                    builder = builder.setSchedulingTime(task.getSchedulingTime().getMillis());

                    int i = 0;
                    if (task.getParameters() != null) {
                        for (Entry<String, Object> entry : task.getParameters().entrySet()) {
                            MapEntry mapEntry = MapEntry.newBuilder().setKey(entry.getKey()).setValue(entry.getValue().toString()).build();
                            builder.addParameters(i++, mapEntry);
                        }
                    }
                    ServerSubmitTaskRequest request = builder.build();

                    WorkerInfo workerInfo = workerSelector.select(task.getGroupId());
                    if (workerInfo != null) {
                        String fullId = task.getFullId();
                        boolean allowed = taskManager.addTask(fullId, workerInfo, appId);
                        if (allowed) {
                            ActorSelection actorSelection = system.actorSelection(workerInfo.getWorkerPath());
                            try {
                                WorkerSubmitTaskResponse response = (WorkerSubmitTaskResponse) FutureUtils.awaitResult(actorSelection, request, 30);
                                if (response.getSuccess()) {
                                    String ip = workerInfo.getIp();
                                    int port = workerInfo.getPort();
                                    if (response.getAccept()) {
                                        LOGGER.debug("Task[{}] was accepted by worker[{}:{}]", fullId, ip, port);
                                        continue;
                                    } else {
                                        LOGGER.warn("Task[{}] was rejected by worker[{}:{}]", fullId, ip, port);
                                        taskRetryScheduler.addTask(task, task.getRejectRetries(), task.getRejectInterval(), RetryType.REJECT_RETRY);
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
                        //taskManager.appCounterDecrement(appId);
                    } else {
                        LOGGER.warn("worker not exist, worker group id: {}", task.getGroupId());
                    }

                    taskRetryScheduler.addTask(task, task.getFailedRetries(), task.getFailedInterval(), RetryType.FAILED_RETRY);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            } else {
                yield();
            }
        }
    }
}
