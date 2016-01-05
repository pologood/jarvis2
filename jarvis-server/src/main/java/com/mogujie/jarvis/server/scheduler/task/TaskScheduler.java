/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:42
 */

package com.mogujie.jarvis.server.scheduler.task;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.domain.TaskType;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.dispatcher.TaskManager;
import com.mogujie.jarvis.server.dispatcher.TaskQueue;
import com.mogujie.jarvis.server.domain.RetryType;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.TaskRetryScheduler;
import com.mogujie.jarvis.server.scheduler.event.AddTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.ManualRerunTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RetryTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RunTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RunningEvent;
import com.mogujie.jarvis.server.scheduler.event.ScheduleEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;

/**
 * Scheduler used to handle ready tasks.
 *
 * @author guangming
 *
 */
public class TaskScheduler extends Scheduler {
    private static TaskScheduler instance = new TaskScheduler();

    private TaskScheduler() {
    }

    public static TaskScheduler getInstance() {
        return instance;
    }

    private TaskGraph taskGraph = TaskGraph.INSTANCE;
    private JobService jobService = Injectors.getInjector().getInstance(JobService.class);
    private TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);
    private TaskManager taskManager = Injectors.getInjector().getInstance(TaskManager.class);
    private TaskQueue taskQueue = Injectors.getInjector().getInstance(TaskQueue.class);
    private TaskRetryScheduler retryScheduler = TaskRetryScheduler.INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();

    public void destroy() {
        taskGraph.clear();
    }

    @Override
    public void handleStartEvent(StartEvent event) {
    }

    @Override
    public void handleStopEvent(StopEvent event) {
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleSuccessEvent(SuccessEvent e) {
        long jobId = e.getJobId();
        long taskId = e.getTaskId();
        long scheduleTime = e.getScheduleTime();
        TaskType taskType = e.getTaskType();
        String reason = e.getReason();
        LOGGER.info("start handleSuccessEvent, taskId={}", taskId);

        // update success status
        taskService.updateStatusWithEnd(taskId, TaskStatus.SUCCESS, reason);
        LOGGER.info("update {} with SUCCESS status", taskId);

        List<DAGTask> childTasks = taskGraph.getChildren(taskId);
        if (childTasks != null && !childTasks.isEmpty()) {
            // TaskGraph trigger
            // notify child tasks
            // notice: 如果是串行任务，之前失败了，这里也可以触发自身后续跑起来
            LOGGER.info("notify child tasks {}", childTasks);
            for (DAGTask childTask : childTasks) {
                if (childTask != null && childTask.checkStatus()) {
                    LOGGER.info("child {} pass the status check", childTask);
                    submitTask(childTask);
                }
            }
        } else {
            // 如果是正常调度，交给DAGScheduler触发后续任务
            if (taskType.equals(TaskType.SCHEDULE)) {
                // JobGraph trigger
                ScheduleEvent event = new ScheduleEvent(jobId, taskId, scheduleTime);
                getSchedulerController().notify(event);
            }
        }

        // remove from taskGraph
        taskGraph.removeTask(taskId);
        LOGGER.info("remove {} from taskGraph", taskId);

        // reduce task number
        reduceTaskNum(taskId);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleRunningEvent(RunningEvent e) {
        long taskId = e.getTaskId();
        LOGGER.info("start handleRunningEvent, taskId={}", taskId);
        int workerId = e.getWorkerId();
        taskService.updateStatusWithStart(taskId, TaskStatus.RUNNING, workerId);
        LOGGER.info("update {} with RUNNING status", taskId);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleKilledEvent(KilledEvent e) {
        long taskId = e.getTaskId();
        LOGGER.info("start handleKilledEvent, taskId={}", taskId);
        taskService.updateStatusWithEnd(taskId, TaskStatus.KILLED);
        LOGGER.info("update {} with KILLED status", taskId);
        reduceTaskNum(taskId);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleFailedEvent(FailedEvent e) {
        long taskId = e.getTaskId();
        String reason = e.getReason();
        LOGGER.info("start handleFailedEvent, taskId={}", taskId);
        DAGTask dagTask = taskGraph.getTask(taskId);
        if (dagTask != null) {
            long jobId = dagTask.getJobId();
            Job job = jobService.get(jobId).getJob();
            int failedRetries = job.getFailedAttempts();
            int failedInterval = job.getFailedInterval();

            int attemptId = dagTask.getAttemptId();
            LOGGER.info("attemptId={}, failedRetries={}", attemptId, failedRetries);
            if (attemptId <= failedRetries) {
                attemptId++;
                dagTask.setAttemptId(attemptId);
                Task task = new Task();
                task.setTaskId(taskId);
                task.setAttemptId(attemptId);
                task.setUpdateTime(DateTime.now().toDate());
                task.setStatus(TaskStatus.READY.getValue());
                taskService.updateSelective(task);
                LOGGER.info("update task {}, attemptId={}", taskId, attemptId);
                retryScheduler.addTask(getTaskInfo(dagTask), failedRetries, failedInterval, RetryType.FAILED_RETRY);
                LOGGER.info("add to retryScheduler");
            } else {
                taskService.updateStatusWithEnd(taskId, TaskStatus.FAILED, reason);
                LOGGER.info("update {} with FAILED status", taskId);
                String key = jobId + "_" + taskId;
                retryScheduler.remove(key, RetryType.FAILED_RETRY);
                LOGGER.info("remove from retryScheduler, key={}", key);
            }
        }

        reduceTaskNum(taskId);
    }

    @Subscribe
    public void handleAddTaskEvent(AddTaskEvent e) {
        long jobId = e.getJobId();
        long scheduleTime = e.getScheduleTime();
        LOGGER.info("start handleAddTaskEvent, jobId={}, scheduleTime={}", jobId, scheduleTime);
        Map<Long, List<Long>> dependTaskIdMap = e.getDependTaskIdMap();

        // create new task
        long taskId = taskService.createTaskByJobId(jobId, scheduleTime, TaskType.SCHEDULE);
        LOGGER.info("add new task[{}] to DB", taskId);

        // 如果是串行任务
        if (jobService.get(jobId).getJob().getIsSerial()) {
            // 首先检查自己上一次是否成功
            Task task = taskService.getLastTask(jobId, scheduleTime, TaskType.SCHEDULE);
            if (task != null) {
                if (task.getStatus() != TaskStatus.SUCCESS.getValue()) {
                    // 如果失败，标记为失败
                    String failedReason = "前置串行任务失败";
                    taskService.updateStatusWithEnd(taskId, TaskStatus.FAILED, failedReason);
                }
                // 增加自依赖
                List<Long> dependTaskIds = Lists.newArrayList(task.getTaskId());
                dependTaskIdMap.put(jobId, dependTaskIds);
            }
        }

        // add to taskGraph
        DAGTask dagTask = new DAGTask(jobId, taskId, scheduleTime, dependTaskIdMap);
        taskGraph.addTask(taskId, dagTask);
        LOGGER.info("add {} to taskGraph", dagTask);

        // add task dependency
        if (dependTaskIdMap != null) {
            for (Entry<Long, List<Long>> entry : dependTaskIdMap.entrySet()) {
                List<Long> preTaskIds = entry.getValue();
                for (long parentId : preTaskIds) {
                    taskGraph.addDependency(parentId, taskId);
                }
            }
        }

        // 如果通过依赖检查，提交给任务执行器
        if (dagTask.checkStatus()) {
            submitTask(dagTask);
        }
    }

    @Subscribe
    public void handleRetryTaskEvent(RetryTaskEvent e) {
        long taskId = e.getTaskId();
        LOGGER.info("start handleRetryTaskEvent, taskId={}", taskId);
        Task task = taskService.get(taskId);
        if (task != null) {
            taskService.updateStatus(taskId, TaskStatus.WAITING);
            LOGGER.info("update {} with WAITING status", taskId);

            DAGTask dagTask = taskGraph.getTask(taskId);
            if (dagTask == null) {
                dagTask = new DAGTask(task.getJobId(), taskId, task.getAttemptId(), task.getDataTime().getTime());
                taskGraph.addTask(taskId, dagTask);
                LOGGER.info("add {} to taskGraph", dagTask);
            }
            if (dagTask != null && dagTask.checkStatus()) {
                LOGGER.info("{} pass status check", dagTask);
                int attemptId = dagTask.getAttemptId();
                attemptId++;
                dagTask.setAttemptId(attemptId);
                Task updateTask = new Task();
                updateTask.setTaskId(taskId);
                updateTask.setAttemptId(attemptId);
                updateTask.setUpdateTime(DateTime.now().toDate());
                Job job = jobService.get(dagTask.getJobId()).getJob();
                updateTask.setContent(job.getContent());
                updateTask.setExecuteUser(job.getUpdateUser());
                updateTask.setAppId(job.getAppId());
                updateTask.setWorkerId(job.getWorkerGroupId());
                taskService.updateSelective(updateTask);
                LOGGER.info("update task {}, attemptId={}", taskId, attemptId);
                submitTask(dagTask);
            }
        }
    }

    @Subscribe
    public void handleManulRerunTaskEvent(ManualRerunTaskEvent e) {
        List<Long> taskIdList = e.getTaskIdList();
        LOGGER.info("start handleRetryTaskEvent, taskIdList={}", taskIdList);
        for (Long taskId : taskIdList) {
            DAGTask dagTask = taskGraph.getTask(taskId);
            if (dagTask != null && dagTask.checkStatus()) {
                LOGGER.info("{} pass status check", dagTask);
                submitTask(dagTask);
            }
        }
    }

    @Subscribe
    public void handleRunTaskEvent(RunTaskEvent e) {
        long taskId = e.getTaskId();
        LOGGER.info("start handleRunTaskEvent, taskId={}", taskId);
        DAGTask dagTask = taskGraph.getTask(taskId);
        if (dagTask != null) {
            submitTask(dagTask);
        }
    }

    @VisibleForTesting
    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    private void submitTask(DAGTask dagTask) {
        // update status to ready
        taskService.updateStatus(dagTask.getTaskId(), TaskStatus.READY);
        LOGGER.info("update {} with READY status", dagTask.getTaskId());

        // submit to TaskQueue
        TaskDetail taskDetail = getTaskInfo(dagTask);
        if (taskDetail != null) {
            taskQueue.put(taskDetail);
        }
    }

    private TaskDetail getTaskInfo(DAGTask dagTask) {
        String fullId = IdUtils.getFullId(dagTask.getJobId(), dagTask.getTaskId(), dagTask.getAttemptId());
        TaskDetail taskDetail = null;
        long jobId = dagTask.getJobId();
        Job job = jobService.get(jobId).getJob();
        taskDetail = TaskDetail.newTaskDetailBuilder()
                .setFullId(fullId)
                .setTaskName(job.getJobName())
                .setAppName(jobService.getAppName(jobId))
                .setUser(job.getSubmitUser())
                .setPriority(job.getPriority())
                .setContent(job.getContent())
                .setTaskType(job.getJobType())
                .setParameters(JsonHelper.fromJson2JobParams(job.getParams()))
                .setDataTime(new DateTime(dagTask.getDataTime()))
                .setGroupId(job.getWorkerGroupId())
                .setFailedRetries(job.getFailedAttempts())
                .setFailedInterval(job.getFailedInterval())
                .setRejectRetries(job.getRejectAttempts())
                .setRejectInterval(job.getRejectInterval())
                .build();
        return taskDetail;
    }

    private void reduceTaskNum(long taskId) {
        Task task = taskService.get(taskId);
        if (task != null) {
            taskManager.appCounterDecrement(task.getAppId());
        }
    }

}
