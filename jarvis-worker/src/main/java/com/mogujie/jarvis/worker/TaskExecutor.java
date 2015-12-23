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

import com.mogujie.jarvis.core.AbstractLogCollector;
import com.mogujie.jarvis.core.ProgressReporter;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.exeception.AcceptanceException;
import com.mogujie.jarvis.core.exeception.TaskException;
import com.mogujie.jarvis.protocol.ReportTaskStatusProtos.WorkerReportTaskStatusRequest;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.WorkerSubmitTaskResponse;
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

    public TaskExecutor(TaskContext taskContext, ActorRef selfActor, ActorRef senderActor, ActorSelection serverActor) {
        this.taskContext = taskContext;
        this.selfActor = selfActor;
        this.senderActor = senderActor;
        this.serverActor = serverActor;
    }

    @Override
    public void run() {
        Pair<Class<? extends AbstractTask>, List<AcceptanceStrategy>> pair = TaskConfigUtils.getRegisteredJobs()
                .get(taskContext.getTaskDetail().getTaskType());
        List<AcceptanceStrategy> strategies = pair.getSecond();
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
            Constructor<? extends AbstractTask> constructor = pair.getFirst().getConstructor(TaskContext.class);
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

            reporter.report(1);
            logCollector.collectStderr("", true);
            logCollector.collectStdout("", true);
            taskPool.remove(fullId);
        } catch (Exception e) {
            senderActor.tell(WorkerSubmitTaskResponse.newBuilder().setAccept(false).setMessage(e.getMessage()).build(), selfActor);
        }
    }

}
