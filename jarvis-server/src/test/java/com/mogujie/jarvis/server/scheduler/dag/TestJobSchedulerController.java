/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月25日 下午1:19:51
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;

/**
 * @author guangming
 *
 */
public class TestJobSchedulerController extends TestSchedulerBase {
    private long jobAId = 1;
    private long jobBId = 2;
    private long jobCId = 3;
    private long taskAId = 0;
    private long taskBId = 0;
    private long taskCId = 0;

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        taskService.deleteTaskAndRelation(taskAId);
        taskService.deleteTaskAndRelation(taskBId);
        taskService.deleteTaskAndRelation(taskCId);
    }

    /**
     *    A   B
     *     \ /
     *      C
     */
    @Test
    public void testHandleSuccessEvent1() throws Exception {
        jobGraph.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        jobGraph.addJob(jobBId, new DAGJob(jobBId, DAGJobType.TIME), null);
        jobGraph.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId, jobBId));
        Assert.assertEquals(1, jobGraph.getChildren(jobAId).size());
        Assert.assertEquals(jobCId, (long) jobGraph.getChildren(jobAId).get(0).getFirst());
        Assert.assertEquals(1, jobGraph.getChildren(jobBId).size());
        Assert.assertEquals(jobCId, (long) jobGraph.getChildren(jobBId).get(0).getFirst());
        Assert.assertEquals(2, jobGraph.getParents(jobCId).size());
        // jobA time ready
        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
        controller.notify(timeEventA);
        Assert.assertEquals(1, taskGraph.getTaskMap().size());
        Assert.assertEquals(1, taskQueue.size());

        // jobB time ready, start to schedule jobC
        TimeReadyEvent timeEventB = new TimeReadyEvent(jobBId);
        controller.notify(timeEventB);
        Assert.assertEquals(3, taskGraph.getTaskMap().size());
        Assert.assertEquals(2, taskQueue.size());

        List<Long> taskIds = new ArrayList<Long>(taskGraph.getTaskMap().keySet());
        Collections.sort(taskIds);
        taskAId = taskIds.get(0);
        taskBId = taskIds.get(1);
        taskCId = taskIds.get(2);
        // jobA success, taskScheduler remove jobA from readyTable
        SuccessEvent successEventA = new SuccessEvent(jobAId, taskAId);
        controller.notify(successEventA);
        Assert.assertEquals(2, taskGraph.getTaskMap().size());
        Assert.assertEquals(2, taskQueue.size());

        // jobB success, taskScheduler remove jobB from readyTable, and jobC run
        SuccessEvent successEventB = new SuccessEvent(jobBId, taskBId);
        controller.notify(successEventB);
        Assert.assertEquals(1, taskGraph.getTaskMap().size());
        Assert.assertEquals(3, taskQueue.size());

        // jobC success, taskScheduler remove jobC from readyTable
        SuccessEvent successEventC = new SuccessEvent(jobCId, taskCId);
        controller.notify(successEventC);
        Assert.assertEquals(0, taskGraph.getTaskMap().size());
    }

    /**
     *      A
     *     / \
     *    B   C
     */
    @Test
    public void testHandleSuccessEvent2() throws Exception {
        jobGraph.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        jobGraph.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        jobGraph.addJob(jobCId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        Assert.assertEquals(2, jobGraph.getChildren(jobAId).size());
        Assert.assertEquals(1, jobGraph.getParents(jobBId).size());
        Assert.assertEquals(1, jobGraph.getParents(jobCId).size());

        // jobA time ready, start to schedule jobB and jobC
        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
        controller.notify(timeEventA);
        Assert.assertEquals(3, taskGraph.getTaskMap().size());
        Assert.assertEquals(1, taskQueue.size());

        List<Long> taskIds = new ArrayList<Long>(taskGraph.getTaskMap().keySet());
        Collections.sort(taskIds);
        taskAId = taskIds.get(0);

        // jobA success
        SuccessEvent eventA = new SuccessEvent(jobAId, taskAId);
        controller.notify(eventA);
        // jobB and jobC run, taskScheduler remove jobA from readyTable
        Assert.assertEquals(2, taskGraph.getTaskMap().size());
        Assert.assertEquals(3, taskQueue.size());
    }
}
