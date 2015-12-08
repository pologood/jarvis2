/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月10日 上午11:24:33
 */

package com.mogujie.jarvis.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Range;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.dao.generate.JobMapper;
import com.mogujie.jarvis.dao.generate.TaskMapper;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.dto.generate.TaskExample;

/**
 * @author guangming
 */
@Service
public class TaskService {
    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskDependService taskDependService;

    @Autowired
    private JobMapper jobMapper;

    public Task get(long taskId) {
        return taskMapper.selectByPrimaryKey(taskId);
    }

    public long insert(Task record) {
        taskMapper.insert(record);
        return record.getTaskId();
    }

    public void updateSelective(Task record) {
        taskMapper.updateByPrimaryKeySelective(record);
    }

    public void updateProgress(long taskId, float progress) {
        Task record = new Task();
        record.setTaskId(taskId);
        record.setProgress(progress);
        record.setUpdateTime(new Date());
        taskMapper.updateByPrimaryKeySelective(record);
    }

    public void updateWorkerId(long taskId, int workerId) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setWorkerId(workerId);
        task.setUpdateTime(new Date());
        taskMapper.updateByPrimaryKeySelective(task);
    }

    public List<Task> getTasks(List<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return null;
        }
        TaskExample example = new TaskExample();
        example.createCriteria().andTaskIdIn(taskIds);
        return taskMapper.selectByExample(example);
    }

    public long createTaskByJobId(long jobId, long scheduleTime) {
        Task record = new Task();
        record.setJobId(jobId);
        record.setAttemptId(1);
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        record.setCreateTime(currentTime);
        record.setUpdateTime(currentTime);
        record.setScheduleTime(new Date(scheduleTime));
        record.setStatus(TaskStatus.WAITING.getValue());
        record.setProgress((float) 0);
        Job job = jobMapper.selectByPrimaryKey(jobId);
        record.setExecuteUser(job.getSubmitUser());
        record.setContent(job.getContent());
        record.setParams(job.getParams());
        record.setAppId(job.getAppId());
        taskMapper.insert(record);
        return record.getTaskId();
    }

    public List<Task> getTasksByStatusNotIn(List<Integer> statusList) {
        TaskExample example = new TaskExample();
        example.createCriteria().andStatusNotIn(statusList).andJobIdNotEqualTo(0L);
        return taskMapper.selectByExample(example);
    }

    public List<Task> getTasksByStatus(List<Integer> statusList) {
        TaskExample example = new TaskExample();
        example.createCriteria().andStatusIn(statusList).andJobIdNotEqualTo(0L);
        return taskMapper.selectByExample(example);
    }

    public List<Long> getTaskIdsByJobIdsBetween(List<Long> jobIds, Date start, Date end) {
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdIn(jobIds).andScheduleTimeBetween(start, end).andJobIdNotEqualTo(0L);
        List<Task> tasks = taskMapper.selectByExample(example);
        List<Long> taskIdList = new ArrayList<Long>();
        if (tasks != null) {
            for (Task task : tasks) {
                taskIdList.add(task.getTaskId());
            }
        }
        return taskIdList;
    }

    public void updateStatusWithStart(long taskId, TaskStatus status, int workerId) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setStatus(status.getValue());
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        task.setExecuteStartTime(currentTime);
        task.setUpdateTime(currentTime);
        task.setWorkerId(workerId);
        taskMapper.updateByPrimaryKeySelective(task);
    }

    public void updateStatusWithEnd(long taskId, TaskStatus status) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setStatus(status.getValue());
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        task.setExecuteEndTime(currentTime);
        task.setUpdateTime(currentTime);
        taskMapper.updateByPrimaryKeySelective(task);
    }

    public void updateStatusWithEnd(long taskId, TaskStatus status, Map<Long, List<Long>> childTaskMap) {
        updateStatusWithEnd(taskId, status);
        taskDependService.storeChild(taskId, childTaskMap);
    }

    public void updateStatus(long taskId, TaskStatus status) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setStatus(status.getValue());
        task.setUpdateTime(DateTime.now().toDate());
        taskMapper.updateByPrimaryKeySelective(task);
    }

    public List<Long> getDependTaskIds(long myJobId, long preJobId, long scheduleTime, DependencyExpression dependencyExpression) {
        List<Task> tasks;
        if (dependencyExpression != null) {
            tasks = getTasksBetween(preJobId, dependencyExpression.getRange(new DateTime(scheduleTime)));
        } else {
            long preScheduleTime = getPreScheduleTime(myJobId, scheduleTime);
            tasks = getTasksBetween(preJobId, new DateTime(preScheduleTime), new DateTime(scheduleTime));
        }
        List<Long> taskIds = new ArrayList<Long>();
        for (Task task : tasks) {
            taskIds.add(task.getTaskId());
        }
        return taskIds;
    }

    public List<Task> getTasksBetween(long jobId, Range<DateTime> range) {
        if (jobId == 0 || range == null) {
            return null;
        }
        return getTasksBetween(jobId, range.lowerEndpoint(), range.upperEndpoint());
    }

    public List<Task> getTasksBetween(long jobId, DateTime start, DateTime end) {
        if (jobId == 0 || start == null || end == null) {
            return null;
        }
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdEqualTo(jobId).andScheduleTimeBetween(start.toDate(), end.toDate()).andJobIdNotEqualTo(0L);
        return taskMapper.selectByExample(example);
    }

    public Task getTaskByJobIdAndScheduleTime(long jobId, long scheduleTime) {
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdEqualTo(jobId).andScheduleTimeEqualTo(new Date(scheduleTime));
        List<Task> taskList = taskMapper.selectByExample(example);
        if (taskList != null && !taskList.isEmpty()) {
            return taskList.get(0);
        }
        return null;
    }

    public List<Task> getTasksBetween(Date start, Date end) {
        TaskExample example = new TaskExample();
        example.createCriteria().andScheduleTimeBetween(start, end).andJobIdNotEqualTo(0L);
        return taskMapper.selectByExample(example);
    }

    public long getPreScheduleTime(long jobId, long scheduleTime) {
        if (jobId == 0 || scheduleTime == 0) {
            return 0;
        }
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdEqualTo(jobId).andScheduleTimeLessThan(new DateTime(scheduleTime * 1000L).toDate()).andJobIdNotEqualTo(0L);
        example.setOrderByClause("taskId desc");
        List<Task> tasks = taskMapper.selectByExample(example);
        if (tasks == null || tasks.isEmpty()) {
            return 0;
        }
        return new DateTime(tasks.get(0).getScheduleTime()).getMillis();
    }

    @VisibleForTesting
    public void deleteTaskAndRelation(long taskId) {
        if (taskId > 0) {
            taskMapper.deleteByPrimaryKey(taskId);
            taskDependService.remove(taskId);
        }
    }

}
