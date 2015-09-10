/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月10日 下午5:59:07
 */

package com.mogujie.jarvis.server.scheduler.dag;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.server.scheduler.JobScheduleType;
import com.mogujie.jarvis.server.scheduler.dag.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.TimeReadyEvent;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJobFactory;
import com.mogujie.jarvis.server.scheduler.dag.status.CachedDependStatus;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;

/**
 * @author guangming
 *
 */
public class TestDAGScheduler {
    private DAGJob jobA;
    private DAGJob jobB;
    private DAGJob jobC;
    private DAGScheduler dagScheduler = DAGScheduler.getInstance();
    private TaskScheduler taskScheduler = TaskScheduler.getInstance();

    @Before
    public void setup() throws Exception {
        taskScheduler.setEnableTest();
        jobA = DAGJobFactory.createDAGJob(JobScheduleType.CRONTAB, 1,
                new CachedDependStatus(), JobDependencyStrategy.ALL);
        jobB = DAGJobFactory.createDAGJob(JobScheduleType.CRONTAB, 2,
                new CachedDependStatus(), JobDependencyStrategy.ALL);
        jobC = DAGJobFactory.createDAGJob(JobScheduleType.DEPENDENCY, 3,
                new CachedDependStatus(), JobDependencyStrategy.ALL);
    }

    @After
    public void tearDown() throws Exception {
        dagScheduler.clear();
        taskScheduler.clear();
    }

    /**
     *   A   B
     *    \ /
     *     C
     */
    @Test
    public void testHandleSuccessEvent() throws Exception {
        dagScheduler.addJob(jobA.getJobId(), jobA, null);
        dagScheduler.addJob(jobB.getJobId(), jobB, null);
        dagScheduler.addJob(jobC.getJobId(), jobC, Sets.newHashSet(jobA.getJobId(),jobB.getJobId()));
        // jobA time ready
        TimeReadyEvent timeEventA = new TimeReadyEvent(jobA.getJobId());
        dagScheduler.handleTimeReadyEvent(timeEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobB time ready
        TimeReadyEvent timeEventB = new TimeReadyEvent(jobB.getJobId());
        dagScheduler.handleTimeReadyEvent(timeEventB);
        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
        // jobA success
        SuccessEvent eventA = new SuccessEvent(jobA.getJobId(), 1);
        dagScheduler.handleSuccessEvent(eventA);
        // jobB success
        SuccessEvent eventB = new SuccessEvent(jobB.getJobId(), 2);
        dagScheduler.handleSuccessEvent(eventB);
        // jobC run
        Assert.assertEquals(3, taskScheduler.getReadyTable().size());

    }
}
