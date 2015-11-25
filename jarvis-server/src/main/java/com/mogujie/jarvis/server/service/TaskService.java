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

import com.google.common.collect.Range;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dao.TaskMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.dto.TaskExample;

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

    public void update(Task task) {
        taskMapper.updateByPrimaryKey(task);
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
        record.setStatus(JobStatus.WAITING.getValue());
        record.setProgress((float) 0);
        Job job = jobMapper.selectByPrimaryKey(jobId);
        record.setExecuteUser(job.getSubmitUser());
        record.setContent(job.getContent());
        taskMapper.insert(record);
        return record;
    }

    public List<Task> getTasksByStatus(Integer status) {
        TaskExample example = new TaskExample();
        example.createCriteria().andStatusEqualTo(status);
        return taskMapper.selectByExample(example);
    }

    public List<Task> getTasksByStatus(List<Integer> statusList) {
        TaskExample example = new TaskExample();
        example.createCriteria().andStatusIn(statusList);
        return taskMapper.selectByExample(example);
    }

    public void updateStatusWithStart(long taskId, JobStatus status) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        task.setStatus(status.getValue());
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        task.setExecuteStartTime(currentTime);
        task.setUpdateTime(currentTime);
        taskMapper.updateByPrimaryKey(task);
    }

    public void updateStatusWithEnd(long taskId, JobStatus status) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        task.setStatus(status.getValue());
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        task.setExecuteEndTime(currentTime);
        task.setUpdateTime(currentTime);
        taskMapper.updateByPrimaryKey(task);
    }

    public void updateStatus(long taskId, JobStatus status) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        task.setStatus(status.getValue());
        task.setUpdateTime(DateTime.now().toDate());
        taskMapper.updateByPrimaryKey(task);
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
            status.add(task.getStatus() == JobStatus.SUCCESS.getValue());
        }
        return status;
    }


}
