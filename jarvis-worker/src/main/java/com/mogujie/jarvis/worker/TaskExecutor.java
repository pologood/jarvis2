/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月7日 下午1:44:53
 */

package com.mogujie.jarvis.worker;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;
import com.mogujie.jarvis.core.AbstractLogCollector;
import com.mogujie.jarvis.core.AbstractTask;
import com.mogujie.jarvis.core.ProgressReporter;
import com.mogujie.jarvis.core.TaskContext;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.exception.AcceptanceException;
import com.mogujie.jarvis.core.exception.TaskException;
import com.mogujie.jarvis.protocol.ReportTaskStatusProtos.WorkerReportTaskStatusRequest;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.WorkerSubmitTaskResponse;
import com.mogujie.jarvis.worker.domain.TaskEntry;
import com.mogujie.jarvis.worker.status.TaskStateStore;
import com.mogujie.jarvis.worker.status.TaskStateStoreFactory;
import com.mogujie.jarvis.worker.strategy.AcceptanceResult;
import com.mogujie.jarvis.worker.strategy.AcceptanceStrategy;
import com.mogujie.jarvis.worker.util.TaskConfigUtils;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;

public class TaskExecutor extends Thread {

    private TaskContext taskContext;
    private ActorRef selfActor;
    private ActorRef senderActor;
    private ActorSelection serverActor;
    private TaskPool taskPool = TaskPool.INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

    public TaskExecutor(TaskContext taskContext, ActorRef selfActor, ActorRef senderActor, ActorSelection serverActor) {
        this.taskContext = taskContext;
        this.selfActor = selfActor;
        this.senderActor = senderActor;
        this.serverActor = serverActor;
    }

    @Override
    public void run() {
        TaskEntry taskEntry = TaskConfigUtils.getRegisteredTasks().get(taskContext.getTaskDetail().getJobType());
        List<AcceptanceStrategy> strategies = taskEntry.getAcceptanceStrategy();
        for (AcceptanceStrategy strategy : strategies) {
            try {
                AcceptanceResult result = strategy.accept();
                if (!result.isAccepted()) {
                    senderActor.tell(WorkerSubmitTaskResponse.newBuilder().setAccept(false).setSuccess(true).setMessage(result.getMessage()).build(),
                            selfActor);
                    return;
                }
            } catch (AcceptanceException e) {
                senderActor.tell(WorkerSubmitTaskResponse.newBuilder().setAccept(false).setSuccess(false).setMessage(e.getMessage()).build(),
                        selfActor);
                return;
            }
        }

        senderActor.tell(WorkerSubmitTaskResponse.newBuilder().setAccept(true).setSuccess(true).build(), selfActor);
        try {
            @SuppressWarnings("unchecked")
            Constructor<? extends AbstractTask> constructor = ((Class<? extends AbstractTask>) Class.forName(taskEntry.getTaskClass()))
                    .getConstructor(TaskContext.class);
            AbstractTask task = constructor.newInstance(taskContext);
            String fullId = taskContext.getTaskDetail().getFullId();
            ProgressReporter reporter = taskContext.getProgressReporter();
            AbstractLogCollector logCollector = taskContext.getLogCollector();
            taskPool.add(fullId, task);
            serverActor.tell(WorkerReportTaskStatusRequest.newBuilder().setFullId(fullId).setStatus(TaskStatus.RUNNING.getValue())
                    .setTimestamp(System.currentTimeMillis() / 1000).build(), selfActor);
            reporter.report(0);

            boolean result = false;
            try {
                task.preExecute();
                result = task.execute();
                task.postExecute();
            } catch (TaskException e) {
                logCollector.collectStderr(e.getMessage(), true);
            }

            if (result) {
                serverActor.tell(WorkerReportTaskStatusRequest.newBuilder().setFullId(fullId).setStatus(TaskStatus.SUCCESS.getValue())
                        .setTimestamp(System.currentTimeMillis() / 1000).build(), selfActor);
            } else {
                serverActor.tell(WorkerReportTaskStatusRequest.newBuilder().setFullId(fullId).setStatus(TaskStatus.FAILED.getValue())
                        .setTimestamp(System.currentTimeMillis() / 1000).build(), selfActor);
            }

            TaskStateStore taskStateStore = TaskStateStoreFactory.getInstance();
            taskStateStore.delete(taskContext.getTaskDetail().getFullId());

            reporter.report(1);
            logCollector.collectStderr("", true);
            logCollector.collectStdout("", true);
            taskPool.remove(fullId);
        } catch (RuntimeException e) {
            Throwables.propagate(e);
        } catch (Exception e) {
            LOGGER.error("", e);
            senderActor.tell(WorkerSubmitTaskResponse.newBuilder().setAccept(false).setMessage(e.getMessage()).build(), selfActor);
        }
    }

}
