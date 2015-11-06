/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月6日 下午2:37:57
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
public class DefaultTaskStatusChecker extends TaskStatusChecker {

    private TaskDependService taskDependService = SpringContext.getBean(TaskDependService.class);
    private Map<Long, AbstractTaskStatus> jobStatusMap = new ConcurrentHashMap<Long, AbstractTaskStatus>();

    /**
     * @param jobId
     * @param taskId
     */
    public DefaultTaskStatusChecker(long jobId, long taskId, Map<Long, Set<Long>> dependTaskIdMap) {
        super(jobId, taskId);
        if (dependTaskIdMap != null && !dependTaskIdMap.isEmpty()) {
            taskDependService.createTaskDependenices(taskId, dependTaskIdMap);
        }
        if (dependTaskIdMap == null) {
            dependTaskIdMap = taskDependService.getDependTaskIdMap(taskId);
        }
        this.jobStatusMap = loadJobStatus(dependTaskIdMap);
    }

    @Override
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

    private Map<Long, AbstractTaskStatus> loadJobStatus(Map<Long, Set<Long>> dependTaskIdMap) {
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
