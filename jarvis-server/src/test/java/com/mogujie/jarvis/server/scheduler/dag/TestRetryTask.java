/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月2日 上午11:40:13
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.scheduler.event.AddTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RetryTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TestRetryTask extends TestSchedulerBase {
    private long jobAId = 1;
    private TaskService taskService = SpringContext.getBean(TaskService.class);

    @Test
    public void testRetryTask1() {
        AddTaskEvent addTaskEvent = new AddTaskEvent(jobAId, null);
        controller.notify(addTaskEvent);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        Assert.assertEquals(1, taskQueue.size());
        List<Long> taskIds = new ArrayList<Long>(taskScheduler.getReadyTable().keySet());
        Collections.sort(taskIds);
        long taskAId = taskIds.get(0);
        Task task = taskService.get(taskAId);
        Assert.assertEquals(TaskStatus.READY.getValue(), task.getStatus().intValue());
        Assert.assertEquals(1, task.getAttemptId().intValue());

        SuccessEvent successEvent = new SuccessEvent(jobAId, taskAId);
        controller.notify(successEvent);
        task = taskService.get(taskAId);
        Assert.assertEquals(TaskStatus.SUCCESS.getValue(), task.getStatus().intValue());
        Assert.assertEquals(1, task.getAttemptId().intValue());

        RetryTaskEvent retryEvent = new RetryTaskEvent(Lists.newArrayList(taskAId));
        controller.notify(retryEvent);
        task = taskService.get(taskAId);
        Assert.assertEquals(TaskStatus.READY.getValue(), task.getStatus().intValue());
        Assert.assertEquals(2, task.getAttemptId().intValue());
    }

    @Test
    public void testManualRerunTask() {

    }
}
