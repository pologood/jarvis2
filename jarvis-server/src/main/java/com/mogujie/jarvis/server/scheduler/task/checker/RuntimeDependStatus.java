/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月5日 上午9:56:41
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import java.util.List;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;
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
    public RuntimeDependStatus(long myJobId, long preJobId, CommonStrategy commonStrategy) {
        super(myJobId, preJobId, commonStrategy);
        this.taskService = SpringContext.getBean(TaskService.class);
    }

    @Override
    public boolean check() {
        boolean finishDependency = false;
        CommonStrategy strategy = getCommonStrategy();
        // 多个执行计划中任意一次成功即算成功
        if (strategy.equals(CommonStrategy.ANYONE)) {
            List<Task> dependTasks = taskService.getTasks(dependTaskIds);
            for (Task task : dependTasks) {
                if (task.getStatus() == JobStatus.SUCCESS.getValue()) {
                    finishDependency = true;
                    break;
                }
            }
        } else if (strategy.equals(CommonStrategy.LASTONE)) {
            // 多个执行计划中最后一次成功算成功
            Task task = taskService.getLastTask(dependTaskIds);
            if (task != null && task.getStatus() == JobStatus.SUCCESS.getValue()) {
                finishDependency = true;
            }
        } else if (strategy.equals(CommonStrategy.ALL)) {
            // 多个执行计划中所有都成功才算成功
            finishDependency = true;
            List<Task> dependTasks = taskService.getTasks(dependTaskIds);
            for (Task task : dependTasks) {
                if (task.getStatus() != JobStatus.SUCCESS.getValue()) {
                    finishDependency = false;
                    break;
                }
            }
        }
        return finishDependency;
    }

    public List<Long> getDependTaskIds() {
        return dependTaskIds;
    }

    public void setDependTaskIds(List<Long> dependTaskIds) {
        this.dependTaskIds = dependTaskIds;
    }
}
