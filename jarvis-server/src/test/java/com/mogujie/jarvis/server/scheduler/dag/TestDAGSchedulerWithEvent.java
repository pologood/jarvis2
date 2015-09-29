/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月10日 下午5:59:07
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.server.domain.ModifyDependEntry;
import com.mogujie.jarvis.server.domain.ModifyDependEntry.MODIFY_OPERATION;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependCheckerFactory;
import com.mogujie.jarvis.server.scheduler.dag.checker.DummyDAGDependChecker;
import com.mogujie.jarvis.server.scheduler.event.AddJobEvent;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyDependencyEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobFlagEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;

/**
 * @author guangming
 *
 */
public class TestDAGSchedulerWithEvent {
    private long jobAId = 1;
    private long jobBId = 2;
    private long jobCId = 3;
    private DAGScheduler dagScheduler = DAGScheduler.getInstance();
    private TaskScheduler taskScheduler = TaskScheduler.getInstance();
    private Configuration conf = ConfigUtils.getServerConfig();

    @Before
    public void setup() throws Exception {
        conf.setProperty(DAGDependCheckerFactory.DAG_DEPEND_CHECKER_KEY,
                DummyDAGDependChecker.class.getName());
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
    public void testHandleSuccessEvent1() throws Exception {
        AddJobEvent addEventA = new AddJobEvent(jobAId, null,
                DAGJobType.TIME);
        AddJobEvent addEventB = new AddJobEvent(jobBId, null,
                DAGJobType.TIME);
        AddJobEvent addEventC = new AddJobEvent(jobCId, Sets.newHashSet(jobAId, jobBId),
                DAGJobType.DEPEND);
        dagScheduler.handleAddJobEvent(addEventA);
        dagScheduler.handleAddJobEvent(addEventB);
        dagScheduler.handleAddJobEvent(addEventC);
        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobAId).get(0).getFirst());
        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobBId).get(0).getFirst());
        Assert.assertEquals(2, dagScheduler.getParents(jobCId).size());
        // jobA time ready
        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
        dagScheduler.handleTimeReadyEvent(timeEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobB time ready
        TimeReadyEvent timeEventB = new TimeReadyEvent(jobBId);
        dagScheduler.handleTimeReadyEvent(timeEventB);
        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
        // jobA success
        SuccessEvent successEventA = new SuccessEvent(jobAId, 1);
        dagScheduler.handleSuccessEvent(successEventA);
        // jobB success
        SuccessEvent successEventB = new SuccessEvent(jobBId, 2);
        dagScheduler.handleSuccessEvent(successEventB);
        // jobC run
        Assert.assertEquals(3, taskScheduler.getReadyTable().size());
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
        dagScheduler.handleAddJobEvent(addEventA);
        dagScheduler.handleAddJobEvent(addEventB);
        dagScheduler.handleAddJobEvent(addEventC);
        Assert.assertEquals(2, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
        Assert.assertEquals(1, dagScheduler.getParents(jobCId).size());
        // jobA time ready
        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
        dagScheduler.handleTimeReadyEvent(timeEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobA success
        SuccessEvent eventA = new SuccessEvent(jobAId, 1);
        dagScheduler.handleSuccessEvent(eventA);
        // jobB and jobC run
        Assert.assertEquals(3, taskScheduler.getReadyTable().size());
    }

    /**
     *     A
     */
    @Test
    public void testHandleFailedEvent() throws Exception {
        AddJobEvent addEventA = new AddJobEvent(jobAId, null, DAGJobType.TIME);
        dagScheduler.handleAddJobEvent(addEventA);
        // jobA time ready
        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
        dagScheduler.handleTimeReadyEvent(timeEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // failed 1, retry
        FailedEvent eventA = new FailedEvent(jobAId, 1);
        taskScheduler.handleFailedEvent(eventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // failed 2, retry
        taskScheduler.handleFailedEvent(eventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // failed 3, retry
        taskScheduler.handleFailedEvent(eventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // failed 4, remove
        taskScheduler.handleFailedEvent(eventA);
        Assert.assertEquals(0, taskScheduler.getReadyTable().size());
    }

    /**
     *              A
     *     A        |
     *    / \  -->  B
     *   B   C      |
     *              C
     */
    @Test
    public void testHandleModifyDependencyEvent() throws Exception {
        AddJobEvent addEventA = new AddJobEvent(jobAId, null,
                DAGJobType.TIME);
        AddJobEvent addEventB = new AddJobEvent(jobBId, Sets.newHashSet(jobAId),
                DAGJobType.DEPEND);
        AddJobEvent addEventC = new AddJobEvent(jobCId, Sets.newHashSet(jobAId),
                DAGJobType.DEPEND);
        dagScheduler.handleAddJobEvent(addEventA);
        dagScheduler.handleAddJobEvent(addEventB);
        dagScheduler.handleAddJobEvent(addEventC);
        Assert.assertEquals(2, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
        Assert.assertEquals(1, dagScheduler.getParents(jobCId).size());
        List<ModifyDependEntry> dependEntries = new ArrayList<ModifyDependEntry>();
        dependEntries.add(new ModifyDependEntry(MODIFY_OPERATION.DEL, jobAId));
        dependEntries.add(new ModifyDependEntry(MODIFY_OPERATION.ADD, jobBId));
        ModifyDependencyEvent modifyEventC = new ModifyDependencyEvent(jobCId, dependEntries);
        dagScheduler.handleModifyDependency(modifyEventC);
        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
        Assert.assertEquals(1, dagScheduler.getParents(jobCId).size());
        Assert.assertEquals(jobBId, (long)dagScheduler.getParents(jobCId).get(0).getFirst());
    }

    /**
     *     A (TIME)              A (TIME)
     *     |                -->  |
     *     B (DEPEND_TIME)       B (DEPEND)
     */
    @Test
    public void testHandleModifyJobEvent() throws Exception {
        AddJobEvent addEventA = new AddJobEvent(jobAId, null,
                DAGJobType.TIME);
        AddJobEvent addEventB = new AddJobEvent(jobBId, Sets.newHashSet(jobAId),
                DAGJobType.DEPEND_TIME);
        dagScheduler.handleAddJobEvent(addEventA);
        dagScheduler.handleAddJobEvent(addEventB);
        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
        // jobA time ready
        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
        dagScheduler.handleTimeReadyEvent(timeEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobA success, jobB need time ready falg, so running task=1
        SuccessEvent successEventA = new SuccessEvent(jobAId, 1);
        dagScheduler.handleSuccessEvent(successEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // modify jobB from DEPEND_TIME to DEPENDENCY, so don't need time ready flag
        ModifyJobEvent modifyEventB = new ModifyJobEvent(jobBId, false, false);
        dagScheduler.handleModifyJobEvent(modifyEventB);
        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
    }

    /**
     *   A   B (ENABLE)      A   B (DISABLE)
     *    \ /           -->   \ /
     *     C                   C
     */
    @Test
    public void testModifyJobFlag1() throws Exception {
        AddJobEvent addEventA = new AddJobEvent(jobAId, null,
                DAGJobType.TIME);
        AddJobEvent addEventB = new AddJobEvent(jobBId, null,
                DAGJobType.TIME);
        AddJobEvent addEventC = new AddJobEvent(jobCId, Sets.newHashSet(jobAId, jobBId),
                DAGJobType.DEPEND);
        dagScheduler.handleAddJobEvent(addEventA);
        dagScheduler.handleAddJobEvent(addEventB);
        dagScheduler.handleAddJobEvent(addEventC);
        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobAId).get(0).getFirst());
        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobBId).get(0).getFirst());
        Assert.assertEquals(2, dagScheduler.getParents(jobCId).size());
        // jobA time ready
        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
        dagScheduler.handleTimeReadyEvent(timeEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobA success
        SuccessEvent successEventA = new SuccessEvent(jobAId, 1);
        dagScheduler.handleSuccessEvent(successEventA);
        // jobB disable
        ModifyJobFlagEvent jobFlagEventB = new ModifyJobFlagEvent(jobBId, JobFlag.DISABLE);
        dagScheduler.handleModifyJobFlagEvent(jobFlagEventB);
        // jobC run
        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
        // jobC has two parents
        Assert.assertEquals(2, dagScheduler.getParents(jobCId).size());
    }

    /**
     *   A   B (ENABLE)      A   B (DELETED)
     *    \ /           -->   \ /
     *     C                   C
     */
    @Test
    public void testModifyJobFlag2() throws Exception {
        AddJobEvent addEventA = new AddJobEvent(jobAId, null, DAGJobType.TIME);
        AddJobEvent addEventB = new AddJobEvent(jobBId, null, DAGJobType.TIME);
        AddJobEvent addEventC = new AddJobEvent(jobCId, Sets.newHashSet(jobAId, jobBId),
                DAGJobType.DEPEND);
        dagScheduler.handleAddJobEvent(addEventA);
        dagScheduler.handleAddJobEvent(addEventB);
        dagScheduler.handleAddJobEvent(addEventC);
        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobAId).get(0).getFirst());
        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobBId).get(0).getFirst());
        Assert.assertEquals(2, dagScheduler.getParents(jobCId).size());
        // jobA time ready
        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
        dagScheduler.handleTimeReadyEvent(timeEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobA success
        SuccessEvent successEventA = new SuccessEvent(jobAId, 1);
        dagScheduler.handleSuccessEvent(successEventA);
        // jobB deleted
        ModifyJobFlagEvent jobFlagEventB = new ModifyJobFlagEvent(jobBId, JobFlag.DELETED);
        dagScheduler.handleModifyJobFlagEvent(jobFlagEventB);
        // jobC run
        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
        // jobC has one parent
        Assert.assertEquals(1, dagScheduler.getParents(jobCId).size());
    }
}
