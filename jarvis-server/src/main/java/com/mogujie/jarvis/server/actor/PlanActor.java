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

import org.joda.time.DateTime;
import org.mybatis.guice.transactional.Transactional;

import com.google.common.collect.Range;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.protocol.GeneratePlanProtos.RestServerGenereateAllPlanRequest;
import com.mogujie.jarvis.protocol.GeneratePlanProtos.ServerGenereateAllPlanResponse;
import com.mogujie.jarvis.protocol.RemovePlanProtos.RestServerRemovePlanRequest;
import com.mogujie.jarvis.protocol.RemovePlanProtos.ServerRemovePlanResponse;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.RemoveTaskEvent;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.plan.PlanGenerator;
import com.mogujie.jarvis.server.scheduler.task.DAGTask;
import com.mogujie.jarvis.server.scheduler.task.TaskGraph;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeSchedulerFactory;
import com.mogujie.jarvis.server.service.TaskService;

import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author guangming
 *
 */
public class PlanActor extends UntypedActor {

    private TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);

    private TaskGraph taskGraph = TaskGraph.INSTANCE;
    private JobSchedulerController controller = JobSchedulerController.getInstance();

    public static Props props() {
        return Props.create(PlanActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerRemovePlanRequest) {
            RestServerRemovePlanRequest msg = (RestServerRemovePlanRequest) obj;
            removePlan(msg);
        } else if (obj instanceof RestServerGenereateAllPlanRequest) {
            RestServerGenereateAllPlanRequest msg = (RestServerGenereateAllPlanRequest) obj;
            generateAllPlan(msg);
        } else {
            unhandled(obj);
        }
    }

    /**
     * 删除已有的某一个执行计划
     *
     * @param msg
     */
    @Transactional
    private void removePlan(RestServerRemovePlanRequest msg) {
        ServerRemovePlanResponse response;
        long taskId = msg.getTaskId();
        Task task = taskService.get(taskId);
        if (task != null) {
            long jobId = task.getJobId();
            DateTime scheduleTime = new DateTime(task.getScheduleTime());
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
                    response = ServerRemovePlanResponse.newBuilder().setSuccess(false)
                            .setMessage(taskId + "有后置任务: " + childrenJson + ", 删除后置任务将不再依赖于" + taskId).build();
                } else {
                    taskService.updateStatus(taskId, TaskStatus.REMOVED);
                    timeScheduler.removePlan(new ExecutionPlanEntry(jobId, scheduleTime, taskId));
                    controller.notify(new RemoveTaskEvent(jobId, taskId));
                    response = ServerRemovePlanResponse.newBuilder().setSuccess(true).build();
                }
            } else {
                // 不询问，即强制删除
                taskService.updateStatus(taskId, TaskStatus.REMOVED);
                timeScheduler.removePlan(new ExecutionPlanEntry(jobId, scheduleTime, taskId));
                controller.notify(new RemoveTaskEvent(jobId, taskId));
                response = ServerRemovePlanResponse.newBuilder().setSuccess(true).build();
            }
        } else {
            response = ServerRemovePlanResponse.newBuilder().setSuccess(false).setMessage("Task: taskId= " + taskId + " 不存在!").build();
        }
        getSender().tell(response, getSelf());
    }

    /**
     * 生成一段时间的所有任务
     *
     * @param msg
     */
    private void generateAllPlan(RestServerGenereateAllPlanRequest msg) {
        DateTime start = new DateTime(msg.getStartDate());
        DateTime end = new DateTime(msg.getEndDate());
        Range<DateTime> range = Range.openClosed(start, end);

        ServerGenereateAllPlanResponse response;
        try {
            PlanGenerator planGenerator = new PlanGenerator();
            planGenerator.generateAllPlan(range);
        } catch (Exception e) {
            response = ServerGenereateAllPlanResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
        }
        response = ServerGenereateAllPlanResponse.newBuilder().setSuccess(true).build();
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
        list.add(new ActorEntry(RestServerGenereateAllPlanRequest.class, ServerGenereateAllPlanResponse.class, MessageType.GENERAL));
        return list;
    }
}
