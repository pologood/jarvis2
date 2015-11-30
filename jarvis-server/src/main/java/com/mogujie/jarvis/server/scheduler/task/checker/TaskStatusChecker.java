/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月4日 下午7:14:59
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.DependencyStrategyExpression;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;


/**
 * @author guangming
 *
 */
public class TaskStatusChecker {

//    private TaskDependService taskDependService = SpringContext.getBean(TaskDependService.class);
    private JobService jobService = SpringContext.getBean(JobService.class);
    private TaskService taskService = SpringContext.getBean(TaskService.class);
    private Map<Long, TaskDependStatus> jobStatusMap = new ConcurrentHashMap<Long, TaskDependStatus>();
    private long myJobId;
    private long myTaskId;

    public TaskStatusChecker(long jobId, long taskId, long scheduleTime, Map<Long, List<Long>> dependTaskIdMap) {
        this.myJobId = jobId;
        this.myTaskId = taskId;
        if (dependTaskIdMap != null && !dependTaskIdMap.isEmpty()) {
            this.jobStatusMap = convertToJobStatus(scheduleTime, dependTaskIdMap);
        }
    }

    public TaskStatusChecker(long jobId, long taskId, long scheduleTime) {
        this.myJobId = jobId;
        this.myTaskId = taskId;
        this.jobStatusMap = loadJobStatus(myJobId, scheduleTime);
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
        for (Entry<Long, TaskDependStatus> entry : jobStatusMap.entrySet()) {
            TaskDependStatus status = entry.getValue();
            if (!status.check()) {
                finishStatus = false;
                break;
            }
        }
        return finishStatus;
    }

//    public List<Long> getChildTaskIds() {
//        return taskDependService.getChildTaskIds(myTaskId);
//    }

    public List<Long> getDependTaskIds() {
        List<Long> dependTaskIds = new ArrayList<Long>();
        for (TaskDependStatus status : jobStatusMap.values()) {
            dependTaskIds.addAll(status.getDependTaskIds());
        }
        return dependTaskIds;
    }

    private Map<Long, TaskDependStatus> loadJobStatus(long myJobId, long scheduleTime) {
        Map<Long, TaskDependStatus> jobStatusMap = new ConcurrentHashMap<Long, TaskDependStatus>();
        Map<Long, JobDependencyEntry> dependencyMap = jobService.get(myJobId).getDependencies();
        for (Long preJobId : dependencyMap.keySet()) {
            JobDependencyEntry dependencyEntry = dependencyMap.get(preJobId);
            DependencyStrategyExpression commonStrategy = dependencyEntry.getDependencyStrategyExpression();
            DependencyExpression dependencyExpression = dependencyEntry.getDependencyExpression();
            List<Long> dependTaskIds = taskService.getDependTaskIds(myJobId, preJobId, scheduleTime, dependencyExpression);
            TaskDependStatus taskStatus = new TaskDependStatus(dependTaskIds, commonStrategy);
            jobStatusMap.put(preJobId, taskStatus);
        }
        return jobStatusMap;
    }

    private Map<Long, TaskDependStatus> convertToJobStatus(long scheduleTime, Map<Long, List<Long>> dependTaskIdMap) {
        Map<Long, TaskDependStatus> jobStatusMap = new ConcurrentHashMap<Long, TaskDependStatus>();
        for (Entry<Long, List<Long>> entry : dependTaskIdMap.entrySet()) {
            long preJobId = entry.getKey();
            Map<Long, JobDependencyEntry> dependencyMap = jobService.get(myJobId).getDependencies();
            if (dependencyMap != null && dependencyMap.containsKey(preJobId)) {
                JobDependencyEntry dependencyEntry = dependencyMap.get(preJobId);
                DependencyStrategyExpression commonStrategy = dependencyEntry.getDependencyStrategyExpression();
                List<Long> dependTaskIds = entry.getValue();
                if (dependTaskIds == null || dependTaskIds.isEmpty()) {
                    DependencyExpression dependencyExpression = dependencyEntry.getDependencyExpression();
                    dependTaskIds = taskService.getDependTaskIds(myJobId, preJobId, scheduleTime, dependencyExpression);
                }
                TaskDependStatus taskStatus = new TaskDependStatus(dependTaskIds, commonStrategy);
                jobStatusMap.put(preJobId, taskStatus);
            }
        }
        return jobStatusMap;
    }
}
