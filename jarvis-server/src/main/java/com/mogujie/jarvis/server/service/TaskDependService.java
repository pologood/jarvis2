/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月4日 下午3:31:53
 */

package com.mogujie.jarvis.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.dao.TaskDependMapper;
import com.mogujie.jarvis.dto.TaskDepend;
import com.mogujie.jarvis.dto.TaskDependExample;

/**
 * @author guangming
 *
 */
@Service
public class TaskDependService {
    @Autowired
    private TaskDependMapper taskDependMapper;

    public List<Long> getDependTaskIds(long taskId) {
        TaskDependExample example = new TaskDependExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        List<TaskDepend> records = taskDependMapper.selectByExample(example);
        List<Long> taskIds = new ArrayList<Long>();
        for (TaskDepend record : records) {
            taskIds.add(record.getPreTaskId());
        }
        return taskIds;
    }

    public List<Long> getChildTaskIds(long taskId) {
        TaskDependExample example = new TaskDependExample();
        example.createCriteria().andPreTaskIdEqualTo(taskId);
        List<TaskDepend> records = taskDependMapper.selectByExample(example);
        List<Long> taskIds = new ArrayList<Long>();
        for (TaskDepend record : records) {
            taskIds.add(record.getTaskId());
        }
        return taskIds;
    }

    public Map<Long, Set<Long>> getDependTaskIdMap(long taskId) {
        TaskDependExample example = new TaskDependExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        List<TaskDepend> records = taskDependMapper.selectByExample(example);
        Map<Long, Set<Long>> dependTaskIdMap = new HashMap<Long, Set<Long>>();
        for (TaskDepend record : records) {
            long preJobId = record.getPreJobId();
            if (dependTaskIdMap.containsKey(preJobId)) {
                Set<Long> dependTasks = dependTaskIdMap.get(preJobId);
                dependTasks.add(record.getPreTaskId());
            } else {
                Set<Long> dependTasks = Sets.newHashSet();
                dependTasks.add(record.getPreTaskId());
                dependTaskIdMap.put(preJobId, dependTasks);
            }
        }

        return dependTaskIdMap;
    }

    public void createTaskDependenices(long taskId, Map<Long, Set<Long>> dependTaskIdMap) {
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        for (Entry<Long, Set<Long>> entry : dependTaskIdMap.entrySet()) {
            long preJobId = entry.getKey();
            Set<Long> preTaskIds = entry.getValue();
            for (Long preTaskId : preTaskIds) {
                TaskDepend record = new TaskDepend();
                record.setTaskId(taskId);
                record.setPreJobId(preJobId);
                record.setPreTaskId(preTaskId);
                record.setCreateTime(currentTime);
                taskDependMapper.insert(record);
            }
        }
    }
}
