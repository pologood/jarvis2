/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月6日 下午5:36:59
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.mogujie.jarvis.server.scheduler.event.AddTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;

/**
 * @author guangming
 *
 */
public class TestTaskSchedulerWithEvent extends TestSchedulerBase {
    private long jobAId = 1;
    private long jobBId = 2;
    private long jobCId = 3;
    private long taskAId = 0;
    private long taskBId = 0;

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        taskService.deleteTaskAndRelation(taskAId);
        taskService.deleteTaskAndRelation(taskBId);
    }

    @Test
    public void testHandleAddTaskEvent1() {
        AddTaskEvent addTaskEventA = new AddTaskEvent(jobAId, null);
        taskScheduler.handleAddTaskEvent(addTaskEventA);
        Assert.assertEquals(1, taskGraph.getTaskMap().size());
        Assert.assertEquals(1, taskQueue.size());
        List<Long> taskIds = new ArrayList<Long>(taskGraph.getTaskMap().keySet());
        taskAId = taskIds.get(0);

        AddTaskEvent addTaskEventB = new AddTaskEvent(jobAId, null);
        taskScheduler.handleAddTaskEvent(addTaskEventB);
        Assert.assertEquals(2, taskGraph.getTaskMap().size());
        Assert.assertEquals(2, taskQueue.size());
        taskIds = new ArrayList<Long>(taskGraph.getTaskMap().keySet());
        taskBId = taskIds.get(1);

        Map<Long, List<Long>> dependTaskIdMap = new HashMap<Long, List<Long>>();
        dependTaskIdMap.put(jobAId, Lists.newArrayList(taskAId));
        dependTaskIdMap.put(jobBId, Lists.newArrayList(taskBId));
        AddTaskEvent addTaskEventC = new AddTaskEvent(jobCId, dependTaskIdMap);
        taskScheduler.handleAddTaskEvent(addTaskEventC);
        Assert.assertEquals(3, taskGraph.getTaskMap().size());

        SuccessEvent successEventA = new SuccessEvent(jobAId, taskAId);
        taskScheduler.handleSuccessEvent(successEventA);
        Assert.assertEquals(2, taskGraph.getTaskMap().size());
        SuccessEvent successEventB = new SuccessEvent(jobBId, taskBId);
        taskScheduler.handleSuccessEvent(successEventB);
        Assert.assertEquals(1, taskGraph.getTaskMap().size());
        Assert.assertEquals(3, taskQueue.size());
    }
}
