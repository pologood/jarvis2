/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月5日 上午9:56:41
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import java.util.ArrayList;
import java.util.List;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.expression.DependencyStrategyExpression;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;


/**
 * @author guangming
 *
 */
public class RuntimeDependStatus extends AbstractTaskStatus {

    private List<Long> dependTaskIds;
    private TaskService taskService;

    /**
     * @param myJobId
     * @param preJobId
     * @param commonStrategy
     */
    public RuntimeDependStatus(long myJobId, long preJobId, DependencyStrategyExpression commonStrategy) {
        super(myJobId, preJobId, commonStrategy);
        this.taskService = SpringContext.getBean(TaskService.class);
    }

    public List<Long> getDependTaskIds() {
        return dependTaskIds;
    }

    public void setDependTaskIds(List<Long> dependTaskIds) {
        this.dependTaskIds = dependTaskIds;
    }

    @Override
    protected List<Boolean> getStatusList() {
        List<Task> dependTasks = taskService.getTasks(dependTaskIds);
        List<Boolean> taskStatus = new ArrayList<Boolean>();
        for (Task task : dependTasks) {
            Boolean status = (task.getStatus() == JobStatus.SUCCESS.getValue()) ? true : false;
            taskStatus.add(status);
        }

        return taskStatus;
    }
}
