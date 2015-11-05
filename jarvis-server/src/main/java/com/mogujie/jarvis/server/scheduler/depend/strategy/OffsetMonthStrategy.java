/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午11:24:36
 */

package com.mogujie.jarvis.server.scheduler.depend.strategy;

import java.util.List;

import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class OffsetMonthStrategy extends AbstractOffsetStrategy {

    @Override
    public List<Task> getOffsetTasks(long jobId, int offset) {
        TaskService taskService = SpringContext.getBean(TaskService.class);
        return taskService.getTasksByOffsetMonth(jobId, offset);
    }

}
