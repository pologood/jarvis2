/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午11:22:11
 */

package com.mogujie.jarvis.server.scheduler.dag.strategy;

import java.util.List;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dto.Task;

/**
 * @author guangming
 *
 */
public abstract class AbstractOffsetDependStrategy {

    public boolean check(long jobId, int offset, CommonStrategy commonStrategy) {
        boolean finishDependency = false;
        List<Task> tasks = getOffsetTasks(jobId, offset);
        if (tasks != null && !tasks.isEmpty()) {
            if (commonStrategy.equals(CommonStrategy.ANYONE)) {
                for (Task task : tasks) {
                    if (task.getStatus() == JobStatus.SUCCESS.getValue()) {
                        finishDependency = true;
                        break;
                    }
                }
            } else if (commonStrategy.equals(CommonStrategy.LASTONE)) {
                Task lastone = tasks.get(tasks.size() - 1);
                if (lastone.getStatus() == JobStatus.SUCCESS.getValue()) {
                    finishDependency = true;
                }
            } else if (commonStrategy.equals(CommonStrategy.ALL)) {
                finishDependency = true;
                for (Task task : tasks) {
                    if (task.getStatus() != JobStatus.SUCCESS.getValue()) {
                        finishDependency = false;
                        break;
                    }
                }
            }
        }

        return finishDependency;
    }

    protected abstract List<Task> getOffsetTasks(long jobId, int offset);
}
