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

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private JobMapper jobMapper;

    public Task get(long taskId) {
        return taskMapper.selectByPrimaryKey(taskId);
    }

    public long insert(Task record) {
        return taskMapper.insert(record);
    }

    public void update(Task record) {
        taskMapper.updateByPrimaryKey(record);
    }

    public void updateProgress(long taskId, float progress) {
        Task record = taskMapper.selectByPrimaryKey(taskId);
        if (record != null) {
            record.setProgress(progress);
            record.setUpdateTime(new Date());
            taskMapper.updateByPrimaryKey(record);
        }
    }

    public void updateWorkerId(long taskId, int workerId) {
        Task record = taskMapper.selectByPrimaryKey(taskId);
        if (record != null) {
            record.setWorkerId(workerId);
            record.setUpdateTime(new Date());
            taskMapper.updateByPrimaryKey(record);
        }
    }

    public List<Task> getTasks(List<Long> taskIds) {
        TaskExample example = new TaskExample();
        example.createCriteria().andTaskIdIn(taskIds);
        return taskMapper.selectByExample(example);
    }

    public Task getLastTask(List<Long> taskIds) {
        TaskExample example = new TaskExample();
        example.createCriteria().andTaskIdIn(taskIds);
        example.setOrderByClause("scheduleTime desc");
        return taskMapper.selectByExample(example).get(0);
    }

    public Task createTaskByJobId(long jobId, long scheduleTime) {
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
        taskMapper.insert(record);
        return record;
    }

    public List<Task> getTasksByStatusNotIn(List<Integer> statusList) {
        TaskExample example = new TaskExample();
        example.createCriteria().andStatusNotIn(statusList);
        return taskMapper.selectByExample(example);
    }

    public List<Task> getTasksByStatus(List<Integer> statusList) {
        TaskExample example = new TaskExample();
        example.createCriteria().andStatusIn(statusList);
        return taskMapper.selectByExample(example);
    }

    public List<Long> getTaskIdsByJobIdsBetween(List<Long> jobIds, Date start, Date end) {
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdIn(jobIds).andScheduleTimeBetween(start, end);
        List<Task> tasks = taskMapper.selectByExample(example);
        List<Long> taskIdList = new ArrayList<Long>();
        if (tasks != null) {
            for (Task task : tasks) {
                taskIdList.add(task.getTaskId());
            }
        }
        return taskIdList;
    }

    public void updateStatusWithStart(long taskId, TaskStatus status) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        task.setStatus(status.getValue());
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        task.setExecuteStartTime(currentTime);
        task.setUpdateTime(currentTime);
        taskMapper.updateByPrimaryKey(task);
    }

    public void updateStatusWithEnd(long taskId, TaskStatus status) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        task.setStatus(status.getValue());
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        task.setExecuteEndTime(currentTime);
        task.setUpdateTime(currentTime);
        taskMapper.updateByPrimaryKey(task);
    }

    public void updateStatus(long taskId, TaskStatus status) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        task.setStatus(status.getValue());
        task.setUpdateTime(DateTime.now().toDate());
        taskMapper.updateByPrimaryKey(task);
    }

    public List<Long> getDependTaskIds(long myJobId, long preJobId, long scheduleTime, DependencyExpression dependencyExpression) {
        List<Task> tasks = getTasksBetween(preJobId, dependencyExpression.getRange(new DateTime(scheduleTime)));
        if (tasks == null) {
            return null;
        }
        List<Long> taskIds = new ArrayList<>();
        for(Task task : tasks){
            taskIds.add(task.getTaskId());
        }
        return taskIds;
    }

    public List<Task> getTasksByOffsetDay(long jobId, int offset) {
        DateTime now = DateTime.now();
        Date offsetDay = now.plus(-offset).toDate();
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdEqualTo(jobId)
                .andScheduleTimeBetween(offsetDay, now.toDate());

        return taskMapper.selectByExample(example);
    }

    public List<Task> getTasksByOffsetWeek(long jobId, int offset) {
        DateTime now = DateTime.now();
        Date firstDay = now.plusWeeks(-offset).withDayOfWeek(1).toDate();
        Date lastDay = now.plusWeeks(-offset).withDayOfWeek(7).toDate();
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdEqualTo(jobId)
                .andScheduleTimeBetween(firstDay, lastDay);
        return taskMapper.selectByExample(example);
    }

    public List<Task> getTasksByOffsetMonth(long jobId, int offset) {
        DateTime now = DateTime.now();
        Date firstDay = now.plusMonths(-offset).dayOfMonth().withMinimumValue().toDate();
        Date lastDay = now.plusMonths(-offset).dayOfMonth().withMaximumValue().toDate();
        TaskExample example = new TaskExample();
        example.createCriteria().andJobIdEqualTo(jobId)
                .andScheduleTimeBetween(firstDay, lastDay);
        return taskMapper.selectByExample(example);
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
        example.createCriteria().andJobIdEqualTo(jobId)
                .andScheduleTimeBetween(start.toDate(), end.toDate());
        return taskMapper.selectByExample(example);
    }

    public List<Task> getTasksBetween(Date start, Date end) {
        TaskExample example = new TaskExample();
        example.createCriteria().andScheduleTimeBetween(start, end);
        return taskMapper.selectByExample(example);
    }

    public List<Boolean> getTaskSuccessStatusBetween(long jobId, Range<DateTime> range) {
        List<Task> tasks = getTasksBetween(jobId, range);
        if (tasks == null) {
            return null;
        }
        List<Boolean> status = new ArrayList<>();
        for(Task task : tasks){
            status.add(task.getStatus() == TaskStatus.SUCCESS.getValue());
        }
        return status;
    }


}
