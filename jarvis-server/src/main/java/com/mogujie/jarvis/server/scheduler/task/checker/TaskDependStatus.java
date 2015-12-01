/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月30日 下午2:51:27
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import java.util.ArrayList;
import java.util.List;

import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.expression.DependencyStrategyExpression;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TaskDependStatus {
    private List<Long> dependTaskIds;
    private TaskService taskService = SpringContext.getBean(TaskService.class);
    private DependencyStrategyExpression commonStrategy;

    public TaskDependStatus(List<Long> dependTaskIds, DependencyStrategyExpression commonStrategy) {
        this.dependTaskIds = dependTaskIds;
        this.commonStrategy = commonStrategy;
    }

    public List<Long> getDependTaskIds() {
        return dependTaskIds;
    }

    public boolean check() {
        return commonStrategy.check(getStatusList());
    }

    private List<Boolean> getStatusList() {
        List<Task> dependTasks = taskService.getTasks(dependTaskIds);
        if(dependTasks == null){
            return null;
        }
        List<Boolean> taskStatus = new ArrayList<Boolean>();
        for (Task task : dependTasks) {
            Boolean status = (task.getStatus() == TaskStatus.SUCCESS.getValue()) ? true : false;
            taskStatus.add(status);
        }

        return taskStatus;
    }
}
