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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.TaskManager;
import com.mogujie.jarvis.server.TaskQueue;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.TaskRetryScheduler;
import com.mogujie.jarvis.server.scheduler.event.AddTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.RetryTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RunTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RunningEvent;
import com.mogujie.jarvis.server.scheduler.event.ScheduleEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;

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

    private JobService jobService = SpringContext.getBean(JobService.class);
    private TaskService taskService = SpringContext.getBean(TaskService.class);
    private TaskManager taskManager = SpringContext.getBean(TaskManager.class);
    private TaskQueue taskQueue = TaskQueue.INSTANCE;
    private TaskRetryScheduler retryScheduler = TaskRetryScheduler.INSTANCE;

    private Map<Long, DAGTask> readyTable = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger();

    public void init(List<Task> readyTasks, List<Task> runningTasks) {
        if (readyTasks != null) {
            for (Task task : readyTasks) {
                DAGTask dagTask = new DAGTask(task.getJobId(), task.getTaskId(), task.getAttemptId());
                readyTable.put(task.getTaskId(), dagTask);
                if (dagTask.checkStatus()) {
                    retryTask(task);
                }
            }
        }

        if (runningTasks != null) {
            for (Task task : runningTasks) {
                DAGTask dagTask = new DAGTask(task.getJobId(), task.getTaskId(), task.getAttemptId());
                readyTable.put(task.getTaskId(), dagTask);
            }
        }
    }

    public void destroy() {
        readyTable.clear();
    }

    @Override
    public void handleStartEvent(StartEvent event) {
    }

    @Override
    public void handleStopEvent(StopEvent event) {
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void handleSuccessEvent(SuccessEvent e) {
        long taskId = e.getTaskId();
        // update task status and remove from readyTable
        updateTaskStatus(taskId, TaskStatus.SUCCESS);
        DAGTask dagTask = readyTable.remove(taskId);

        // notify child tasks
        if (dagTask != null) {
            List<Long> childTasks = dagTask.getChildTaskIds();
            for (Long childId : childTasks) {
                DAGTask childTask = readyTable.get(childId);
                if (childTask != null && childTask.checkStatus()) {
                    LOGGER.debug("DAGTask {} pass the status check when handle SuccessEvent", childTask.getTaskId());
                    submitTask(childTask);
                }
            }
        }

        // reduce task number
        reduceTaskNum(e.getJobId());
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void handleRunningEvent(RunningEvent e) {
        long taskId = e.getTaskId();
        updateTaskStatus(taskId, TaskStatus.RUNNING);
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void handleKilledEvent(KilledEvent e) {
        long taskId = e.getTaskId();
        updateTaskStatus(taskId, TaskStatus.KILLED);
        readyTable.remove(taskId);
        reduceTaskNum(e.getJobId());
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void handleFailedEvent(FailedEvent e) {
        DAGTask dagTask = readyTable.get(e.getTaskId());
        long taskId = e.getTaskId();
        if (dagTask != null) {
            Job job = jobService.get(dagTask.getJobId()).getJob();
            int failedRetries = job.getFailedAttempts();
            int failedInterval = job.getFailedInterval();

            if (dagTask.getAttemptId() <= failedRetries) {
                int attemptId = dagTask.getAttemptId();
                attemptId++;
                dagTask.setAttemptId(attemptId);
                Task task = taskService.get(taskId);
                if (task != null) {
                    task.setAttemptId(attemptId);
                    task.setUpdateTime(DateTime.now().toDate());
                    task.setStatus(TaskStatus.READY.getValue());
                    taskService.update(task);
                    retryScheduler.addTask(getTaskInfo(dagTask), failedRetries, failedInterval);
                }
            } else {
                updateTaskStatus(e.getTaskId(), TaskStatus.FAILED);
                readyTable.remove(taskId);
            }
        }

        reduceTaskNum(e.getJobId());
    }

    @Subscribe
    @Transactional
    public void handleAddTaskEvent(AddTaskEvent e) {
        long jobId = e.getJobId();
        long scheduleTime = e.getScheduleTime();
        Map<Long, Set<Long>> dependTaskIdMap = e.getDependTaskIdMap();

        // create new task
        Task newTask = taskService.createTaskByJobId(jobId, scheduleTime);
        long taskId = newTask.getTaskId();

        DAGTask dagTask = new DAGTask(jobId, taskId, scheduleTime, dependTaskIdMap);
        if (!readyTable.containsKey(taskId)) {
            // add to readyTable
            readyTable.put(taskId, dagTask);

            // 如果通过依赖检查，提交给任务执行器
            if (dagTask.checkStatus()) {
                submitTask(dagTask);
            }

            // send ScheduleEvent
            ScheduleEvent event = new ScheduleEvent(jobId, taskId, scheduleTime);
            getSchedulerController().notify(event);
        }
    }

    @Subscribe
    @Transactional
    public void handleRetryTaskEvent(RetryTaskEvent e) {
        List<Long> taskIdList = e.getTaskIdList();
        boolean runChild = e.isRunChild();
        for (Long taskId : taskIdList) {
            Task task = taskService.get(taskId);
            if (task != null) {
                updateTaskStatus(taskId, TaskStatus.WAITING);

                DAGTask dagTask;
                if (!readyTable.containsKey(taskId)) {
                    dagTask = new DAGTask(task.getJobId(), taskId, task.getAttemptId(), task.getScheduleTime().getTime(), runChild);
                    readyTable.put(taskId, dagTask);
                } else {
                    dagTask = readyTable.get(taskId);
                }
                if (dagTask != null && dagTask.checkStatus()) {
                    int attemptId = dagTask.getAttemptId();
                    attemptId++;
                    dagTask.setAttemptId(attemptId);
                    retryTask(task);
                }
            }
        }
    }

    @Subscribe
    public void handleRunTaskEvent(RunTaskEvent e) {
        long taskId = e.getTaskId();
        DAGTask dagTask = readyTable.get(taskId);
        if (dagTask != null && dagTask.checkStatus()) {
            submitTask(dagTask);
        }
    }

    private void updateTaskStatus(long taskId, TaskStatus status) {
        if (status.equals(TaskStatus.RUNNING)) {
            taskService.updateStatusWithStart(taskId, status);
<<<<<<< HEAD
        } else if (status.equals(TaskStatus.SUCCESS) || status.equals(TaskStatus.FAILED)
                || status.equals(TaskStatus.KILLED)) {
=======
        } else if (status.equals(JobStatus.SUCCESS) || status.equals(JobStatus.FAILED) || status.equals(JobStatus.KILLED)) {
>>>>>>> 767479d89851cbde20558f3e40b50ea0eceaa528
            taskService.updateStatusWithEnd(taskId, status);
        } else {
            taskService.updateStatus(taskId, status);
        }
    }

    @VisibleForTesting
    public Map<Long, DAGTask> getReadyTable() {
        return readyTable;
    }

    @VisibleForTesting
    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    private void retryTask(Task task) {
        long taskId = task.getTaskId();
        DAGTask dagTask = readyTable.get(taskId);
        if (dagTask != null) {
            task.setAttemptId(dagTask.getAttemptId());
            task.setUpdateTime(DateTime.now().toDate());
            taskService.update(task);

            submitTask(dagTask);
        }
    }

    private void submitTask(DAGTask dagTask) {
        // update status to ready
        updateTaskStatus(dagTask.getTaskId(), TaskStatus.READY);

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
        taskDetail = TaskDetail.newTaskDetailBuilder().setFullId(fullId).setTaskName(job.getJobName()).setAppName(jobService.getAppName(jobId))
                .setUser(job.getSubmitUser()).setPriority(job.getPriority()).setContent(job.getContent()).setTaskType(job.getJobType())
                .setParameters(JsonHelper.parseJSON2Map(job.getParams())).setSchedulingTime(dagTask.getScheduleTime()).build();

        return taskDetail;
    }

    private void reduceTaskNum(long jobId) {
        Job job = jobService.get(jobId).getJob();
        taskManager.appCounterDecrement(job.getAppId());
    }
}
