/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月10日 上午11:24:33
 */

package com.mogujie.jarvis.server.service;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dao.TaskMapper;
import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.dto.TaskExample;

/**
 * @author guangming
 *
 */
@Service
public class TaskService {
    @Autowired
    private TaskMapper taskMapper;

    public List<Task> getTasksByStatus(JobStatus status) {
        TaskExample example = new TaskExample();
        example.createCriteria().andStatusEqualTo(status.getValue());
        return taskMapper.selectByExample(example);
    }

    public void updateStatusWithStart(long taskId, JobStatus status) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        task.setStatus(status.getValue());
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        task.setExecuteStartTime(currentTime);
        taskMapper.updateByPrimaryKey(task);
    }

    public void updateStatusWithEnd(long taskId, JobStatus status) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        task.setStatus(status.getValue());
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        task.setExecuteEndTime(currentTime);
        taskMapper.updateByPrimaryKey(task);
    }

    public List<Task> getTasksByOffsetDay(long jobId, int offset) {
        DateTime now = new DateTime();
        Date offsetDay = now.plus(-offset).toDate();
        TaskExample example = new TaskExample();
        example.createCriteria().andDataYmdBetween(offsetDay, now.toDate());
        return taskMapper.selectByExample(example);
    }

    public List<Task> getTasksByOffsetWeek(long jobId, int offset) {
        DateTime now = new DateTime();
        Date firstDay = now.plusWeeks(-offset).withDayOfWeek(1).toDate();
        Date lastDay = now.plusWeeks(-offset).withDayOfWeek(7).toDate();
        TaskExample example = new TaskExample();
        example.createCriteria().andDataYmdBetween(firstDay, lastDay);
        return taskMapper.selectByExample(example);
    }

    public List<Task> getTasksByOffsetMonth(long jobId, int offset) {
        DateTime now = new DateTime();
        Date firstDay = now.plusMonths(-offset).dayOfMonth().withMinimumValue().toDate();
        Date lastDay = now.plusMonths(-offset).dayOfMonth().withMaximumValue().toDate();
        TaskExample example = new TaskExample();
        example.createCriteria().andDataYmdBetween(firstDay, lastDay);
        return taskMapper.selectByExample(example);
    }
}
