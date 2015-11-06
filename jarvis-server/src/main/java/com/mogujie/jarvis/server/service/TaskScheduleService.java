/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月4日 下午2:26:02
 */

package com.mogujie.jarvis.server.service;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.dao.TaskScheduleMapper;
import com.mogujie.jarvis.dto.TaskSchedule;
import com.mogujie.jarvis.dto.TaskScheduleExample;

/**
 * @author guangming
 *
 */
@Service
public class TaskScheduleService {
    @Autowired
    private TaskScheduleMapper taskScheduleMapper;

    public void create(long myJobId, long preJobId, long preTaskId, long scheduleTime) {
        TaskSchedule record = new TaskSchedule();
        record.setJobId(myJobId);
        record.setPreJobId(preJobId);
        record.setPreTaskId(preTaskId);
        DateTime dt = DateTime.now();
        record.setCreateTime(dt.toDate());
        taskScheduleMapper.insert(record);
    }

    public void clearMyStatus(long myJobId) {
        TaskScheduleExample example = new TaskScheduleExample();
        example.createCriteria().andJobIdEqualTo(myJobId);
        taskScheduleMapper.deleteByExample(example);
    }

    public void clearByPreJobId(long myJobId, long preJobId) {
        TaskScheduleExample example = new TaskScheduleExample();
        example.createCriteria().andJobIdEqualTo(myJobId).andPreJobIdEqualTo(preJobId);
        taskScheduleMapper.deleteByExample(example);
    }

    public List<TaskSchedule> getRecordsByMyJobId(long myJobId) {
        TaskScheduleExample example = new TaskScheduleExample();
        example.createCriteria().andJobIdEqualTo(myJobId);
        return taskScheduleMapper.selectByExample(example);
    }

    public List<TaskSchedule> getRecordsByPreJobId(long myJobId, long preJobId) {
        TaskScheduleExample example = new TaskScheduleExample();
        example.createCriteria().andJobIdEqualTo(myJobId).andPreJobIdEqualTo(preJobId);
        return taskScheduleMapper.selectByExample(example);
    }
}
