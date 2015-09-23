/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月10日 上午11:24:33
 */

package com.mogujie.jarvis.server.service;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dao.TaskMapper;
import com.mogujie.jarvis.dto.Task;

/**
 * @author guangming
 *
 */
@Service
public class TaskService {
    @Autowired
    TaskMapper taskMapper;

    public List<Task> getTasksByStatus(JobStatus status) {
        // TODO
        return null;
    }

    public void updateStatusWithStart(long taskId, JobStatus status) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        task.setStatus(status.getValue());
        Date currentTime = new Date();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
        dateTimeFormat.format(currentTime);
        task.setExecuteStartTime(currentTime);
        taskMapper.updateByPrimaryKey(task);
    }

    public void updateStatusWithEnd(long taskId, JobStatus status) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        task.setStatus(status.getValue());
        Date currentTime = new Date();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
        dateTimeFormat.format(currentTime);
        task.setExecuteEndTime(currentTime);
        taskMapper.updateByPrimaryKey(task);
    }

    public List<Task> getTasksByOffsetDay(long jobId, int offset) {
        // TODO
        return null;
    }

    public List<Task> getTasksByOffsetWeek(long jobId, int offset) {
        // TODO
        return null;
    }

    public List<Task> getTasksByOffsetMonth(long jobId, int offset) {
        // TODO
        return null;
    }
}
