/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月25日 下午1:19:51
 */

package com.mogujie.jarvis.server.scheduler.dag;

import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.controller.SyncSchedulerController;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependCheckerFactory;
import com.mogujie.jarvis.server.scheduler.dag.checker.DummyDAGDependChecker;
import com.mogujie.jarvis.server.scheduler.event.AddJobEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;

/**
 * @author guangming
 *
 */
public class TestJobSchedulerController {
    private static JobSchedulerController controller = new SyncSchedulerController();
    private DAGScheduler dagScheduler = DAGScheduler.getInstance();
    private TimeScheduler timeScheduler = TimeScheduler.getInstance();
    private TaskScheduler taskScheduler = TaskScheduler.getInstance();
    private long jobAId = 1;
    private long jobBId = 2;
    private long jobCId = 3;
    private static Configuration conf = ConfigUtils.getServerConfig();

    @Before
    public void setup() {
        controller.register(dagScheduler);
        controller.register(timeScheduler);
        controller.register(taskScheduler);
        conf.setProperty(DAGDependCheckerFactory.DAG_DEPEND_CHECKER_KEY,
                DummyDAGDependChecker.class.getName());
    }

    @After
    public void tearDown() {
        dagScheduler.destroy();
        timeScheduler.destroy();
        taskScheduler.destroy();
    }

    /**
     *   A   B
     *    \ /
     *     C
     */
    @Test
    public void testHandleSuccessEvent1() throws Exception {
        AddJobEvent addEventA = new AddJobEvent(jobAId, null,
                DAGJobType.TIME);
        AddJobEvent addEventB = new AddJobEvent(jobBId, null,
                DAGJobType.TIME);
        AddJobEvent addEventC = new AddJobEvent(jobCId, Sets.newHashSet(jobAId, jobBId),
                DAGJobType.DEPEND);
        controller.notify(addEventA);
        controller.notify(addEventB);
        controller.notify(addEventC);
        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobAId).get(0).getFirst());
        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobBId).get(0).getFirst());
        Assert.assertEquals(2, dagScheduler.getParents(jobCId).size());
        // jobA time ready
        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
        controller.notify(timeEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobB time ready
        TimeReadyEvent timeEventB = new TimeReadyEvent(jobBId);
        controller.notify(timeEventB);
        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
        // jobA success, taskScheduler remove jobA from readyTable
        SuccessEvent successEventA = new SuccessEvent(jobAId, 1);
        controller.notify(successEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobB success, taskScheduler remove jobB from readyTable, and jobC run
        SuccessEvent successEventB = new SuccessEvent(jobBId, 2);
        controller.notify(successEventB);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobC success, taskScheduler remove jobC from readyTable
        SuccessEvent successEventC = new SuccessEvent(jobCId, 3);
        controller.notify(successEventC);
        Assert.assertEquals(0, taskScheduler.getReadyTable().size());
    }

    /**
     *     A
     *    / \
     *   B   C
     */
    @Test
    public void testHandleSuccessEvent2() throws Exception {
        AddJobEvent addEventA = new AddJobEvent(jobAId, null,
                DAGJobType.TIME);
        AddJobEvent addEventB = new AddJobEvent(jobBId, Sets.newHashSet(jobAId),
                DAGJobType.DEPEND);
        AddJobEvent addEventC = new AddJobEvent(jobCId, Sets.newHashSet(jobAId),
                DAGJobType.DEPEND);
        controller.notify(addEventA);
        controller.notify(addEventB);
        controller.notify(addEventC);
        Assert.assertEquals(2, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
        Assert.assertEquals(1, dagScheduler.getParents(jobCId).size());
        // jobA time ready
        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
        controller.notify(timeEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobA success
        SuccessEvent eventA = new SuccessEvent(jobAId, 1);
        controller.notify(eventA);
        // jobB and jobC run, taskScheduler remove jobA from readyTable
        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
    }
}
