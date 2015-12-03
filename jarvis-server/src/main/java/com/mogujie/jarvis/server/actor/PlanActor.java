/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月3日 上午10:50:47
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import akka.actor.UntypedActor;

import com.mogujie.jarvis.core.domain.ActorEntry;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.protocol.RemovePlanProtos.RestServerRemovePlanRequest;
import com.mogujie.jarvis.protocol.RemovePlanProtos.ServerRemovePlanResponse;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.task.DAGTask;
import com.mogujie.jarvis.server.scheduler.task.TaskGraph;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeSchedulerFactory;
import com.mogujie.jarvis.server.service.TaskService;

/**
 * @author guangming
 *
 */
@Named("planActor")
@Scope("prototype")
public class PlanActor extends UntypedActor {

    @Autowired
    private TaskService taskService;

    private TaskGraph taskGraph = TaskGraph.INSTANCE;

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerRemovePlanRequest) {
            RestServerRemovePlanRequest msg = (RestServerRemovePlanRequest) obj;
            removePlan(msg);
        }
    }

    /**
     * 删除已有的某一个执行计划
     *
     * @param msg
     */
    private void removePlan(RestServerRemovePlanRequest msg) {
        ServerRemovePlanResponse response;
        long taskId = msg.getTaskId();
        long jobId = msg.getJobId();
        DateTime scheduleTime = new DateTime(msg.getScheduleTime());
        TimeScheduler timeScheduler = TimeSchedulerFactory.getInstance();
        boolean ask = msg.getAsk();
        // 询问，如果有后继任务，则返回失败，提示错误信息
        if (ask) {
            List<DAGTask> children = taskGraph.getChildren(taskId);
            if (children != null && !children.isEmpty()) {
                List<Long> childIds = new ArrayList<Long>();
                for (DAGTask child : children) {
                    childIds.add(child.getTaskId());
                }
                String childrenJson = JsonHelper.toJson(childIds, List.class);
                response = ServerRemovePlanResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage(taskId + "有后置任务: " + childrenJson + ", 删除会造成后置任务无法跑起来")
                        .build();
            } else {
                taskService.updateStatus(taskId, TaskStatus.REMOVED);
                timeScheduler.removePlan(new ExecutionPlanEntry(jobId, scheduleTime, taskId));
                taskGraph.removeTask(taskId);
                response = ServerRemovePlanResponse.newBuilder().setSuccess(true).build();
            }
        } else {
            // 不询问，即强制删除
            taskService.updateStatus(taskId, TaskStatus.REMOVED);
            timeScheduler.removePlan(new ExecutionPlanEntry(jobId, scheduleTime, taskId));
            taskGraph.removeTask(taskId);
            response = ServerRemovePlanResponse.newBuilder().setSuccess(true).build();
        }
        getSender().tell(response, getSelf());
    }

    /**
     * 处理消息
     *
     * @return
     */
    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestServerRemovePlanRequest.class, ServerRemovePlanResponse.class, MessageType.GENERAL));
        return list;
    }
}
