/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月10日 下午5:59:07
 */

package com.mogujie.jarvis.server.scheduler.dag;

import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.controller.SyncSchedulerController;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependCheckerFactory;
import com.mogujie.jarvis.server.scheduler.dag.checker.DummyDAGDependChecker;
import com.mogujie.jarvis.server.scheduler.event.ScheduleEvent;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.scheduler.task.checker.DummyTaskStatusChecker;
import com.mogujie.jarvis.server.scheduler.task.checker.TaskStatusCheckerFactory;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TestDAGSchedulerWithEvent {
    private long jobAId = 1;
    private long jobBId = 2;
    private long jobCId = 3;
    private long taskAId = 1;
    private long taskBId = 2;
    private long taskCId = 3;
    private long t1 = 1000;
    private long t2 = 2000;
    private long t3 = 3000;
    private static DAGScheduler dagScheduler;
    private static TaskScheduler taskScheduler;
    private static JobSchedulerController controller;
    private static Configuration conf = ConfigUtils.getServerConfig();

    @BeforeClass
    public static void setup() throws Exception {
        conf.clear();
        conf.setProperty(DAGDependCheckerFactory.DAG_DEPEND_CHECKER_KEY,
                DummyDAGDependChecker.class.getName());
        conf.setProperty(TaskStatusCheckerFactory.TASK_STATUS_CHECKER_KEY,
                DummyTaskStatusChecker.class.getName());
        conf.setProperty(SchedulerUtil.ENABLE_TEST_MODE, true);
        controller = SyncSchedulerController.getInstance();
        dagScheduler = SpringContext.getBean(DAGScheduler.class);
        taskScheduler = SpringContext.getBean(TaskScheduler.class);
        controller.register(dagScheduler);
        controller.register(taskScheduler);
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
    public void testHandleScheduleEvent1() throws Exception {
        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.TIME), null);
        dagScheduler.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND),
                Sets.newHashSet(jobAId, jobBId));
        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobAId).get(0).getFirst());
        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobBId).get(0).getFirst());
        Assert.assertEquals(2, dagScheduler.getParents(jobCId).size());
        // schedule jobA
        ScheduleEvent scheduleEventA = new ScheduleEvent(jobAId, taskAId, t1);
        dagScheduler.handleScheduleEvent(scheduleEventA);
        Assert.assertEquals(0, taskScheduler.getReadyTable().size());
        // schedule jobB
        // pass the dependency check, start to schedule jobC
        ScheduleEvent scheduleEventB = new ScheduleEvent(jobBId, taskBId, t2);
        dagScheduler.handleScheduleEvent(scheduleEventB);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
    }

    /**
     *     A
     *    / \
     *   B   C
     */
    @Test
    public void testHandleScheduleEvent2() throws Exception {
        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        dagScheduler.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        Assert.assertEquals(2, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
        Assert.assertEquals(jobAId, (long)dagScheduler.getParents(jobBId).get(0).getFirst());
        Assert.assertEquals(1, dagScheduler.getParents(jobCId).size());
        Assert.assertEquals(jobAId, (long)dagScheduler.getParents(jobCId).get(0).getFirst());
        // schedule jobA
        // pass the dependency check, start to schedule jobB and jobC
        ScheduleEvent scheduleEventA = new ScheduleEvent(jobAId, taskAId, t1);
        dagScheduler.handleScheduleEvent(scheduleEventA);
        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
    }

    /**
     *     A
     *     |
     *     B
     *     |
     *     C
     */
    @Test
    public void testHandleScheduleEvent3() throws Exception {
        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        dagScheduler.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobBId));
        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
        Assert.assertEquals(jobAId, (long)dagScheduler.getParents(jobBId).get(0).getFirst());
        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobBId).get(0).getFirst());
        // schedule jobA
        // pass the dependency check, start to schedule jobB and jobC
        ScheduleEvent scheduleEventA = new ScheduleEvent(jobAId, taskAId, t1);
        dagScheduler.handleScheduleEvent(scheduleEventA);
        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
    }

    /**
     *   A   B
     *    \ /
     *     C
     */
    @Test
    public void testHandleTimeReadyEvent1() throws Exception {
        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.TIME), null);
        dagScheduler.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND),
                Sets.newHashSet(jobAId, jobBId));
        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobAId).get(0).getFirst());
        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobBId).get(0).getFirst());
        Assert.assertEquals(2, dagScheduler.getParents(jobCId).size());
        // jobA time ready
        TimeReadyEvent timeReadyEventA = new TimeReadyEvent(jobAId);
        dagScheduler.handleTimeReadyEvent(timeReadyEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobB time ready
        // pass the dependency check, start to schedule jobC
        TimeReadyEvent timeReadyEventB = new TimeReadyEvent(jobBId);
        dagScheduler.handleTimeReadyEvent(timeReadyEventB);
        Assert.assertEquals(3, taskScheduler.getReadyTable().size());
    }

    /**
     *     A
     *    / \
     *   B   C
     */
    @Test
    public void testHandleTimeReadyEvent2() throws Exception {
        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        dagScheduler.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        Assert.assertEquals(2, dagScheduler.getChildren(jobAId).size());
        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
        Assert.assertEquals(jobAId, (long)dagScheduler.getParents(jobBId).get(0).getFirst());
        Assert.assertEquals(1, dagScheduler.getParents(jobCId).size());
        Assert.assertEquals(jobAId, (long)dagScheduler.getParents(jobCId).get(0).getFirst());
        // jobA time ready
        // pass the dependency check, start to schedule jobB and jobC
        TimeReadyEvent timeReadyEventA = new TimeReadyEvent(jobAId);
        dagScheduler.handleTimeReadyEvent(timeReadyEventA);
        Assert.assertEquals(3, taskScheduler.getReadyTable().size());
    }

    /**
     *     A
     *     |
     *     B
     *     |
     *     C
     */
    @Test
    public void testHandleTimeReadyEvent3() throws Exception {
        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        dagScheduler.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobBId));
        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
        Assert.assertEquals(jobAId, (long)dagScheduler.getParents(jobBId).get(0).getFirst());
        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobBId).get(0).getFirst());
        // jobA time ready
        // pass the dependency check, start to schedule jobB and jobC
        TimeReadyEvent timeReadyEventA = new TimeReadyEvent(jobAId);
        dagScheduler.handleTimeReadyEvent(timeReadyEventA);
        Assert.assertEquals(3, taskScheduler.getReadyTable().size());
    }


//    /**
//     *   A   B
//     *    \ /
//     *     C
//     */
//    @Test
//    public void testHandleSuccessEvent1() throws Exception {
//        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
//        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.TIME), null);
//        dagScheduler.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND),
//                Sets.newHashSet(jobAId, jobBId));
//        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
//        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobAId).get(0).getFirst());
//        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
//        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobBId).get(0).getFirst());
//        Assert.assertEquals(2, dagScheduler.getParents(jobCId).size());
//        // jobA time ready
//        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
//        dagScheduler.handleTimeReadyEvent(timeEventA);
//        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
//        // jobB time ready, start to schedule jobC
//        TimeReadyEvent timeEventB = new TimeReadyEvent(jobBId);
//        dagScheduler.handleTimeReadyEvent(timeEventB);
//        Assert.assertEquals(3, taskScheduler.getReadyTable().size());
//
//        // jobA success
//        SuccessEvent successEventA = new SuccessEvent(jobAId, 1);
//        dagScheduler.handleSuccessEvent(successEventA);
//        // jobB success
//        SuccessEvent successEventB = new SuccessEvent(jobBId, 2);
//        dagScheduler.handleSuccessEvent(successEventB);
//        // jobC run
//        Assert.assertEquals(3, taskScheduler.getReadyTable().size());
//    }
//
//    /**
//     *     A
//     *    / \
//     *   B   C
//     */
//    @Test
//    public void testHandleSuccessEvent2() throws Exception {
//        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
//        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
//        dagScheduler.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
//        Assert.assertEquals(2, dagScheduler.getChildren(jobAId).size());
//        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
//        Assert.assertEquals(1, dagScheduler.getParents(jobCId).size());
//        // jobA time ready
//        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
//        dagScheduler.handleTimeReadyEvent(timeEventA);
//        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
//        // jobA success
//        SuccessEvent eventA = new SuccessEvent(jobAId, 1);
//        dagScheduler.handleSuccessEvent(eventA);
//        // jobB and jobC run
//        Assert.assertEquals(3, taskScheduler.getReadyTable().size());
//    }
//
//    /**
//     *     A
//     */
//    @Test
//    public void testHandleFailedEvent() throws Exception {
//        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
//        // jobA time ready
//        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
//        dagScheduler.handleTimeReadyEvent(timeEventA);
//        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
//        // failed 1, retry
//        FailedEvent eventA = new FailedEvent(jobAId, 1);
//        taskScheduler.handleFailedEvent(eventA);
//        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
//        // failed 2, retry
//        taskScheduler.handleFailedEvent(eventA);
//        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
//        // failed 3, retry
//        taskScheduler.handleFailedEvent(eventA);
//        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
//        // failed 4, remove
//        taskScheduler.handleFailedEvent(eventA);
//        Assert.assertEquals(0, taskScheduler.getReadyTable().size());
//    }
//
//    /**
//     *              A
//     *     A        |
//     *    / \  -->  B
//     *   B   C      |
//     *              C
//     */
//    @Test
//    public void testHandleModifyDependencyEvent() throws Exception {
//        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
//        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
//        dagScheduler.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
//        Assert.assertEquals(2, dagScheduler.getChildren(jobAId).size());
//        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
//        Assert.assertEquals(1, dagScheduler.getParents(jobCId).size());
//        List<ModifyDependEntry> dependEntries = new ArrayList<ModifyDependEntry>();
//        dependEntries.add(new ModifyDependEntry(ModifyOperation.DEL, jobAId));
//        dependEntries.add(new ModifyDependEntry(ModifyOperation.ADD, jobBId));
//        dagScheduler.modifyDependency(jobCId, dependEntries);
//        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
//        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
//        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
//        Assert.assertEquals(1, dagScheduler.getParents(jobCId).size());
//        Assert.assertEquals(jobBId, (long)dagScheduler.getParents(jobCId).get(0).getFirst());
//    }
//
//    /**
//     *     A (TIME)              A (TIME)
//     *     |                -->  |
//     *     B (DEPEND_TIME)       B (DEPEND)
//     */
//    @Test
//    public void testHandleModifyJobEvent() throws Exception {
//        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
//        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND_TIME), Sets.newHashSet(jobAId));
//        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
//        Assert.assertEquals(1, dagScheduler.getParents(jobBId).size());
//        // jobA time ready
//        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
//        dagScheduler.handleTimeReadyEvent(timeEventA);
//        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
//        // jobA success, jobB need time ready falg, so running task=1
//        SuccessEvent successEventA = new SuccessEvent(jobAId, 1);
//        dagScheduler.handleSuccessEvent(successEventA);
//        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
//        // modify jobB from DEPEND_TIME to DEPENDENCY
//        ModifyJobEntry modifyTimeEntry = new ModifyJobEntry(ModifyOperation.DEL, null);
//        Map<ModifyJobType, ModifyJobEntry> modifyMap = new HashMap<ModifyJobType, ModifyJobEntry>();
//        modifyMap.put(ModifyJobType.CRON, modifyTimeEntry);
//        dagScheduler.modifyDAGJobType(jobBId, modifyMap);
//        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
//    }
//
//    /**
//     *   A   B (ENABLE)      A   B (DISABLE)
//     *    \ /           -->   \ /
//     *     C                   C
//     */
//    @Test
//    public void testModifyJobFlag1() throws Exception {
//        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
//        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.TIME), null);
//        dagScheduler.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND),
//                Sets.newHashSet(jobAId, jobBId));
//        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
//        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobAId).get(0).getFirst());
//        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
//        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobBId).get(0).getFirst());
//        Assert.assertEquals(2, dagScheduler.getParents(jobCId).size());
//        // jobA time ready
//        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
//        dagScheduler.handleTimeReadyEvent(timeEventA);
//        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
//        // jobA success
//        SuccessEvent successEventA = new SuccessEvent(jobAId, 1);
//        dagScheduler.handleSuccessEvent(successEventA);
//        // jobB disable
//        dagScheduler.modifyJobFlag(jobBId, JobFlag.DISABLE);
//        // jobC run
//        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
//        // jobC has two parents
//        Assert.assertEquals(2, dagScheduler.getParents(jobCId).size());
//    }
//
//    /**
//     *   A   B (ENABLE)      A   B (DELETED)
//     *    \ /           -->   \ /
//     *     C                   C
//     */
//    @Test
//    public void testModifyJobFlag2() throws Exception {
//        dagScheduler.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
//        dagScheduler.addJob(jobBId, new DAGJob(jobBId, DAGJobType.TIME), null);
//        dagScheduler.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND),
//                Sets.newHashSet(jobAId, jobBId));
//        Assert.assertEquals(1, dagScheduler.getChildren(jobAId).size());
//        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobAId).get(0).getFirst());
//        Assert.assertEquals(1, dagScheduler.getChildren(jobBId).size());
//        Assert.assertEquals(jobCId, (long)dagScheduler.getChildren(jobBId).get(0).getFirst());
//        Assert.assertEquals(2, dagScheduler.getParents(jobCId).size());
//        // jobA time ready
//        TimeReadyEvent timeEventA = new TimeReadyEvent(jobAId);
//        dagScheduler.handleTimeReadyEvent(timeEventA);
//        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
//        // jobA success
//        SuccessEvent successEventA = new SuccessEvent(jobAId, 1);
//        dagScheduler.handleSuccessEvent(successEventA);
//        // jobB deleted
//        dagScheduler.modifyJobFlag(jobBId, JobFlag.DELETED);
//        // jobC run
//        Assert.assertEquals(2, taskScheduler.getReadyTable().size());
//        // jobC has one parent
//        Assert.assertEquals(1, dagScheduler.getParents(jobCId).size());
//    }
}
