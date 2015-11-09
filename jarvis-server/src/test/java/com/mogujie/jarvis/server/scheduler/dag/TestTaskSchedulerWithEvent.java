/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月6日 下午5:36:59
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.server.scheduler.event.AddTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.task.DAGTask;


/**
 * @author guangming
 *
 */
public class TestTaskSchedulerWithEvent extends TestSchedulerBase {
    private long jobAId = 1;
    private long jobBId = 2;
    private long jobCId = 3;
    private long taskAId = 1;
    private long taskBId = 2;
    private long taskCId = 3;
    private long t1 = 1000;
    private long t2 = 2000;
    private long t3 = 3000;

    @Test
    public void testHandleAddTaskEvent1() {
        Map<Long, DAGTask> readyTable = taskScheduler.getReadyTable();
        readyTable.put(taskAId, new DAGTask(jobAId, taskAId, t1));
        readyTable.put(taskBId, new DAGTask(jobBId, taskBId, t1));
        Assert.assertEquals(2, taskScheduler.getReadyTable().size());

        Map<Long, Set<Long>> dependTaskIdMap = new HashMap<Long, Set<Long>>();
        dependTaskIdMap.put(jobAId, Sets.newHashSet(taskAId));
        dependTaskIdMap.put(jobBId, Sets.newHashSet(taskBId));
        AddTaskEvent addTaskEventC = new AddTaskEvent(jobCId, dependTaskIdMap);
        taskScheduler.handleAddTaskEvent(addTaskEventC);
        Assert.assertEquals(3, taskScheduler.getReadyTable().size());
        SuccessEvent successEventA = new SuccessEvent(jobAId, taskAId);
        taskScheduler.handleSuccessEvent(successEventA);
        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
        SuccessEvent successEventB = new SuccessEvent(jobBId, taskBId);
        taskScheduler.handleSuccessEvent(successEventB);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
    }
}
