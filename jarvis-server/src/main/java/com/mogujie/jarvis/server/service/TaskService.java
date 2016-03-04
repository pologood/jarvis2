/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月10日 上午11:24:33
 */

package com.mogujie.jarvis.server.service;

import com.mogujie.jarvis.server.interceptor.OperationLog;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.core.domain.JobContentType;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.domain.TaskType;
import com.mogujie.jarvis.dao.generate.TaskMapper;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.dto.generate.TaskExample;
import com.mogujie.jarvis.dto.generate.TaskHistory;

/**
 * @author guangming
 */
@Singleton
public class TaskService {
    @Inject
    private TaskMapper taskMapper;

    @Inject
    private TaskDependService taskDependService;

    @Inject
    private TaskHistoryService taskHistoryService;

    @Inject
    private JobService jobService;

    @Inject
    private ScriptService scriptService;

    public Task get(long taskId) {
        return taskMapper.selectByPrimaryKey(taskId);
    }

    public List<Task> getTasksByJobId(long jobId) {
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdEqualTo(jobId);
        return taskMapper.selectByExample(example);
    }

    public Task getTaskByJobIdAndScheduleTime(long jobId, long scheduleTime) {
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdEqualTo(jobId).andScheduleTimeEqualTo(new Date(scheduleTime));
        List<Task> tasks = taskMapper.selectByExample(example);
        if (tasks != null && !tasks.isEmpty()) {
            return tasks.get(0);
        } else {
            return null;
        }
    }

    public long insert(Task record) {
        taskMapper.insert(record);
        return record.getTaskId();
    }

    @OperationLog
    public long insertSelective(Task record) {
        taskMapper.insertSelective(record);
        return record.getTaskId();
    }

    public long createTaskByJobId(long jobId, long scheduleTime, long dataTime, TaskType taskType) {
        Task record = new Task();
        record.setJobId(jobId);
        record.setAttemptId(1);
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        record.setCreateTime(currentTime);
        record.setUpdateTime(currentTime);
        record.setScheduleTime(new Date(scheduleTime));
        record.setDataTime(new Date(dataTime));
        record.setStatus(TaskStatus.WAITING.getValue());
        record.setProgress((float) 0);
        Job job = jobService.get(jobId).getJob();
        if (job.getIsTemp()) {
            //如果是临时任务，设置task类型为TEMP
            record.setType(TaskType.TEMP.getValue());
        } else {
            record.setType(taskType.getValue());
        }
        record.setExecuteUser(job.getSubmitUser());
        if (job.getContentType() == JobContentType.SCRIPT.getValue()) {
            int scriptId = Integer.valueOf(job.getContent());
            record.setContent(scriptService.getContentById(scriptId));
        } else {
            record.setContent(job.getContent());
        }
        record.setParams(job.getParams());
        record.setAppId(job.getAppId());
        return insertSelective(record);
    }

    public void updateSelective(Task record) {
        taskMapper.updateByPrimaryKeySelective(record);
    }

    public void updateProgress(long taskId, float progress) {
        Task record = new Task();
        record.setTaskId(taskId);
        record.setProgress(progress);
        record.setUpdateTime(DateTime.now().toDate());
        taskMapper.updateByPrimaryKeySelective(record);
    }

    public void updateStatus(long taskId, TaskStatus status) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setStatus(status.getValue());
        task.setUpdateTime(DateTime.now().toDate());
        taskMapper.updateByPrimaryKeySelective(task);
    }

    public void updateStatusWithStart(long taskId, TaskStatus status, int workerId) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setStatus(status.getValue());
        task.setWorkerId(workerId);
        Date currentTime = DateTime.now().toDate();
        task.setExecuteStartTime(currentTime);
        task.setUpdateTime(currentTime);
        taskMapper.updateByPrimaryKeySelective(task);
    }

    public void updateStatusWithEnd(long taskId, TaskStatus status) {
        updateStatusWithEnd(taskId, status, null);
    }

    @OperationLog
    public void updateStatusWithEnd(long taskId, TaskStatus status, String reason) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setStatus(status.getValue());
        if (reason != null) {
            task.setFinishReason(reason);
        }
        Date currentTime = DateTime.now().toDate();
        task.setExecuteEndTime(currentTime);
        task.setUpdateTime(currentTime);
        taskMapper.updateByPrimaryKeySelective(task);

        insertHistory(taskId);
    }

    public void insertHistory(long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        TaskHistory history = new TaskHistory();
        history.setTaskId(task.getTaskId());
        history.setAttemptId(task.getAttemptId());
        history.setJobId(task.getJobId());
        history.setContent(task.getContent());
        history.setParams(task.getParams());
        history.setScheduleTime(task.getScheduleTime());
        history.setDataTime(task.getDataTime());
        history.setProgress(task.getProgress());
        history.setType(task.getType());
        history.setStatus(task.getStatus());
        history.setFinishReason(task.getFinishReason());
        history.setAppId(task.getAppId());
        history.setWorkerId(task.getWorkerId());
        history.setExecuteUser(task.getExecuteUser());
        history.setExecuteStartTime(task.getExecuteStartTime());
        history.setExecuteEndTime(task.getExecuteEndTime());
        Date currentTime = DateTime.now().toDate();
        history.setCreateTime(currentTime);
        history.setUpdateTime(currentTime);
        taskHistoryService.insertOrUpdate(history);
    }

    public List<Task> getTasksByStatusNotIn(List<Integer> statusList) {
        TaskExample example = new TaskExample();
        example.createCriteria().andStatusNotIn(statusList).andTypeNotEqualTo(TaskType.TEMP.getValue());
        List<Task> tasks = taskMapper.selectByExample(example);
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        return tasks;
    }

    public List<Task> getTasksByStatus(List<Integer> statusList) {
        TaskExample example = new TaskExample();
        example.createCriteria().andStatusIn(statusList).andTypeNotEqualTo(TaskType.TEMP.getValue());
        List<Task> tasks = taskMapper.selectByExample(example);
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        return tasks;
    }

    public List<Task> getTasksBetween(long jobId, Range<DateTime> range) {
        return getTasksBetween(jobId, range, TaskType.SCHEDULE);
    }

    public List<Task> getTasksBetween(long jobId, Range<DateTime> range, TaskType taskType) {
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdEqualTo(jobId)
        .andScheduleTimeBetween(range.lowerEndpoint().toDate(), range.upperEndpoint().toDate())
        .andTypeEqualTo(taskType.getValue());
        List<Task> tasks = taskMapper.selectByExample(example);
        if (tasks == null) {
            tasks = Lists.newArrayList();
        }
        return tasks;
    }

    public Task getLastTask(long jobId, long scheduleTime, TaskType taskType) {
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdEqualTo(jobId)
                .andScheduleTimeLessThan(new DateTime(scheduleTime).toDate())
                .andTypeEqualTo(taskType.getValue());
        example.setOrderByClause("scheduleTime desc");
        List<Task> taskList = taskMapper.selectByExample(example);
        if (taskList != null && !taskList.isEmpty()) {
            return taskList.get(0);
        }
        return null;
    }

    @VisibleForTesting
    public void deleteTaskAndRelation(long taskId) {
        taskMapper.deleteByPrimaryKey(taskId);
        taskDependService.remove(taskId);
        taskHistoryService.deleteByTaskId(taskId);
    }

}
