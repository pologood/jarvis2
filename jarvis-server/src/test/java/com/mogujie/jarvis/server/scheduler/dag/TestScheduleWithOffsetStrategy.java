/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月10日 下午2:13:39
 */

package com.mogujie.jarvis.server.scheduler.dag;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.exeception.JobScheduleException;
import com.mogujie.jarvis.server.scheduler.dag.checker.TaskScheduleFactory;
import com.mogujie.jarvis.server.scheduler.event.ScheduleEvent;
import com.mogujie.jarvis.server.scheduler.task.DAGTask;

/**
 * @author guangming
 *
 */
public class TestScheduleWithOffsetStrategy extends TestSchedulerBase {

    private long jobAId = 1;
    private long jobBId = 2;
    private long jobCId = 3;
    private long taskAId = 1;
    private long taskBId = 2;

    /**
     *   A   B
     *    \ /
     *     C
     *
     *  C在A和B跑之前加入
     *
     * @throws JobScheduleException
     */
    @Test
    public void testCurrentDay1() throws JobScheduleException {
        conf.setProperty(TaskScheduleFactory.JOB_OFFSET_STRATEGY, "cd");

        jobGraph.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        jobGraph.addJob(jobBId, new DAGJob(jobBId, DAGJobType.TIME), null);
        jobGraph.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId, jobBId));
        Assert.assertEquals(1, jobGraph.getChildren(jobAId).size());
        Assert.assertEquals(jobCId, (long) jobGraph.getChildren(jobAId).get(0).getFirst());
        Assert.assertEquals(1, jobGraph.getChildren(jobBId).size());
        Assert.assertEquals(jobCId, (long) jobGraph.getChildren(jobBId).get(0).getFirst());
        Assert.assertEquals(2, jobGraph.getParents(jobCId).size());

        // schedule jobA
        long t1 = DateTime.now().getMillis();
        taskAId = taskService.createTaskByJobId(jobAId, t1);
        taskGraph.addTask(taskAId, new DAGTask(jobAId, taskAId, t1, null));
        ScheduleEvent scheduleEventA = new ScheduleEvent(jobAId, taskAId, t1);
        Assert.assertEquals(1, taskGraph.getTaskMap().size());
        dagScheduler.handleScheduleEvent(scheduleEventA);
        Assert.assertEquals(1, taskGraph.getTaskMap().size());

        // schedule jobB
        // pass the dependency check, start to schedule jobC
        long t2 = DateTime.now().getMillis();
        taskBId = taskService.createTaskByJobId(jobBId, t2);
        taskGraph.addTask(taskBId, new DAGTask(jobBId, taskBId, t2, null));
        ScheduleEvent scheduleEventB = new ScheduleEvent(jobBId, taskBId, t2);
        dagScheduler.handleScheduleEvent(scheduleEventB);
        Assert.assertEquals(3, taskGraph.getTaskMap().size());
    }

    /**
     *   A   B
     *    \ /
     *     C
     *
     *  C在A和B之间加入
     *
     * @throws JobScheduleException
     */
    @Test
    public void testCurrentDay2() throws JobScheduleException {
        conf.setProperty(TaskScheduleFactory.JOB_OFFSET_STRATEGY, "cd");

        jobGraph.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        jobGraph.addJob(jobBId, new DAGJob(jobBId, DAGJobType.TIME), null);

        // schedule jobA
        long t1 = DateTime.now().getMillis();
        taskAId = taskService.createTaskByJobId(jobAId, t1);
        taskGraph.addTask(taskAId, new DAGTask(jobAId, taskAId, t1, null));
        ScheduleEvent scheduleEventA = new ScheduleEvent(jobAId, taskAId, t1);
        Assert.assertEquals(1, taskGraph.getTaskMap().size());
        dagScheduler.handleScheduleEvent(scheduleEventA);
        Assert.assertEquals(1, taskGraph.getTaskMap().size());

        jobGraph.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId, jobBId));

        // schedule jobB
        // pass the dependency check, start to schedule jobC
        long t2 = DateTime.now().getMillis();
        taskBId = taskService.createTaskByJobId(jobBId, t2);
        taskGraph.addTask(taskBId, new DAGTask(jobBId, taskBId, t2, null));
        ScheduleEvent scheduleEventB = new ScheduleEvent(jobBId, taskBId, t2);
        dagScheduler.handleScheduleEvent(scheduleEventB);
        Assert.assertEquals(3, taskGraph.getTaskMap().size());
    }

    /**
     *   A   B
     *    \ /
     *     C
     *
     *  C在A和B之后加入
     *
     * @throws JobScheduleException
     */
    @Test
    public void testCurrentDay3() throws JobScheduleException {
        conf.setProperty(TaskScheduleFactory.JOB_OFFSET_STRATEGY, "cd");

        jobGraph.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        jobGraph.addJob(jobBId, new DAGJob(jobBId, DAGJobType.TIME), null);

        // schedule jobA
        long t1 = DateTime.now().getMillis();
        taskAId = taskService.createTaskByJobId(jobAId, t1);
        taskGraph.addTask(taskAId, new DAGTask(jobAId, taskAId, t1, null));
        ScheduleEvent scheduleEventA = new ScheduleEvent(jobAId, taskAId, t1);
        Assert.assertEquals(1, taskGraph.getTaskMap().size());
        dagScheduler.handleScheduleEvent(scheduleEventA);
        Assert.assertEquals(1, taskGraph.getTaskMap().size());

        // schedule jobB
        // pass the dependency check, start to schedule jobC
        long t2 = DateTime.now().getMillis();
        taskBId = taskService.createTaskByJobId(jobBId, t2);
        taskGraph.addTask(taskBId, new DAGTask(jobBId, taskBId, t2, null));
        ScheduleEvent scheduleEventB = new ScheduleEvent(jobBId, taskBId, t2);
        dagScheduler.handleScheduleEvent(scheduleEventB);
        Assert.assertEquals(2, taskGraph.getTaskMap().size());

        jobGraph.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId, jobBId));

        Assert.assertEquals(3, taskGraph.getTaskMap().size());
    }
}
