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
import java.util.concurrent.ConcurrentHashMap;

import com.mogujie.jarvis.server.service.TaskDependService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TaskStatusChecker {

    private long myJobId;
    private long myTaskId;
    private TaskDependService taskDependService = SpringContext.getBean(TaskDependService.class);
    private Map<Long, AbstractTaskStatus> jobStatusMap = new ConcurrentHashMap<Long, AbstractTaskStatus>();

    public TaskStatusChecker(long jobId, long taskId) {
        this.myJobId = jobId;
        this.myTaskId = taskId;
        this.jobStatusMap = loadJobStatus();
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

    private Map<Long, AbstractTaskStatus> loadJobStatus() {
        Map<Long, AbstractTaskStatus> jobStatusMap = new ConcurrentHashMap<Long, AbstractTaskStatus>();
        Map<Long, List<Long>> dependTaskMap = taskDependService.getDependTaskIdMap(myTaskId);
        for (Entry<Long, List<Long>> entry : dependTaskMap.entrySet()) {
            long preJobId = entry.getKey();
            AbstractTaskStatus taskStatus = TaskStatusFactory.create(myJobId, preJobId);
            if (taskStatus instanceof RuntimeDependStatus) {
                List<Long> dependTasks = dependTaskMap.get(preJobId);
                ((RuntimeDependStatus) taskStatus).setDependTaskIds(dependTasks);
                jobStatusMap.put(preJobId, taskStatus);
            }
        }
        return jobStatusMap;
    }
}
