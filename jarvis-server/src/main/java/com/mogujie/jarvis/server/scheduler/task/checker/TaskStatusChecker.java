/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月4日 下午7:14:59
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Lists;
import com.mogujie.jarvis.server.service.TaskDependService;
import com.mogujie.jarvis.server.util.SpringContext;


/**
 * @author guangming
 *
 */
public class TaskStatusChecker {

    private TaskDependService taskDependService = SpringContext.getBean(TaskDependService.class);
    private Map<Long, AbstractTaskStatus> jobStatusMap = new ConcurrentHashMap<Long, AbstractTaskStatus>();
    private long myJobId;
    private long myTaskId;

    public TaskStatusChecker(long jobId, long taskId, Map<Long, Set<Long>> dependTaskIdMap) {
        this.myJobId = jobId;
        this.myTaskId = taskId;
        if (dependTaskIdMap != null && !dependTaskIdMap.isEmpty()) {
            taskDependService.createTaskDependenices(taskId, dependTaskIdMap);
            this.jobStatusMap = convertToJobStatus(dependTaskIdMap);
        }
    }

    public TaskStatusChecker(long jobId, long taskId) {
        this.myJobId = jobId;
        this.myTaskId = taskId;
        this.jobStatusMap = loadJobStatus(taskId);
    }

    public long getMyJobId() {
        return myJobId;
    }

    public void setMyJobId(long myJobId) {
        this.myJobId = myJobId;
    }

    public long getMyTaskId() {
        return myTaskId;
    }

    public void setMyTaskId(long myTaskId) {
        this.myTaskId = myTaskId;
    }

    public boolean checkStatus() {
        boolean finishStatus = true;
        for (Entry<Long, AbstractTaskStatus> entry : jobStatusMap.entrySet()) {
            AbstractTaskStatus status = entry.getValue();
            if (!status.check()) {
                finishStatus = false;
                break;
            }
        }
        return finishStatus;
    }

    public List<Long> getChildTaskIds() {
        return taskDependService.getChildTaskIds(myTaskId);
    }

    private Map<Long, AbstractTaskStatus> loadJobStatus(long taskId) {
        Map<Long, Set<Long>> dependTaskIdMap = taskDependService.getDependTaskIdMap(taskId);
        return convertToJobStatus(dependTaskIdMap);
    }

    private Map<Long, AbstractTaskStatus> convertToJobStatus(Map<Long, Set<Long>> dependTaskIdMap) {
        Map<Long, AbstractTaskStatus> jobStatusMap = new ConcurrentHashMap<Long, AbstractTaskStatus>();
        for (Entry<Long, Set<Long>> entry : dependTaskIdMap.entrySet()) {
            long preJobId = entry.getKey();
            AbstractTaskStatus taskStatus = TaskStatusFactory.create(getMyJobId(), preJobId);
            if (taskStatus instanceof RuntimeDependStatus) {
                Set<Long> dependTasks = dependTaskIdMap.get(preJobId);
                List<Long> dependTaskList = Lists.newArrayList();
                dependTaskList.addAll(dependTasks);
                ((RuntimeDependStatus) taskStatus).setDependTaskIds(dependTaskList);
                jobStatusMap.put(preJobId, taskStatus);
            }
        }
        return jobStatusMap;
    }
}
