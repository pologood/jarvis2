/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 上午11:33:47
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.exeception.JobScheduleException;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.plan.NextDayPlanGenerator;
import com.mogujie.jarvis.server.scheduler.plan.PlanGenerator;

/**
 * @author guangming
 *
 */
public class TestPlan extends TestSchedulerBase {
    private long jobAId = 1;
    private long jobBId = 2;
    private long jobCId = 3;
    private long jobDId = 4;
    private long jobEId = 5;
    private long jobFId = 6;
    private long t1 = 1000;
    private long t2 = 2000;
    private long t3 = 3000;
    private long t4 = 3000;

    /**
     *     A
     *    / \
     *   B   C
     */
    @Test
    public void testGeneratePlan1() throws JobScheduleException {
        jobGraph.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        jobGraph.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        jobGraph.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        Assert.assertEquals(2, jobGraph.getChildren(jobAId).size());
        Assert.assertEquals(1, jobGraph.getParents(jobBId).size());
        Assert.assertEquals(jobAId, (long)jobGraph.getParents(jobBId).get(0).getFirst());
        Assert.assertEquals(1, jobGraph.getParents(jobCId).size());
        Assert.assertEquals(jobAId, (long)jobGraph.getParents(jobCId).get(0).getFirst());
        // jobA time ready
        // pass the dependency check, start to schedule jobB and jobC
        TimeReadyEvent timeReadyEventA = new TimeReadyEvent(jobAId, t1);
        dagScheduler.handleTimeReadyEvent(timeReadyEventA);
        Assert.assertEquals(3, taskScheduler.getReadyTable().size());
        // jobA time ready
        TimeReadyEvent timeReadyEventB = new TimeReadyEvent(jobAId, t2);
        dagScheduler.handleTimeReadyEvent(timeReadyEventB);
        Assert.assertEquals(6, taskScheduler.getReadyTable().size());
    }

    /**
     *   A   B
     *    \ /
     *     C
     */
    @Test
    public void testGeneratePlan2() throws JobScheduleException {
        jobGraph.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        jobGraph.addJob(jobBId, new DAGJob(jobBId, DAGJobType.TIME), null);
        jobGraph.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId, jobBId));
        Assert.assertEquals(1, jobGraph.getChildren(jobAId).size());
        Assert.assertEquals(1, jobGraph.getChildren(jobBId).size());
        Assert.assertEquals(2, jobGraph.getParents(jobCId).size());
        // jobA time ready
        TimeReadyEvent timeReadyEventA = new TimeReadyEvent(jobAId, t1);
        dagScheduler.handleTimeReadyEvent(timeReadyEventA);
        Assert.assertEquals(1, taskScheduler.getReadyTable().size());
        // jobB time ready
        TimeReadyEvent timeReadyEventB = new TimeReadyEvent(jobBId, t2);
        dagScheduler.handleTimeReadyEvent(timeReadyEventB);
        Assert.assertEquals(3, taskScheduler.getReadyTable().size());

        // jobA time ready
        timeReadyEventA = new TimeReadyEvent(jobAId, t3);
        dagScheduler.handleTimeReadyEvent(timeReadyEventA);
        Assert.assertEquals(4, taskScheduler.getReadyTable().size());
        // jobB time ready
        timeReadyEventB = new TimeReadyEvent(jobBId, t4);
        dagScheduler.handleTimeReadyEvent(timeReadyEventB);
        Assert.assertEquals(6, taskScheduler.getReadyTable().size());
    }

    @Test
    public void testGenerateNextDayPlan() throws JobScheduleException {
        jobGraph.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        jobGraph.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        jobGraph.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        jobGraph.addJob(jobDId, new DAGJob(jobDId, DAGJobType.TIME), null);
        jobGraph.addJob(jobEId, new DAGJob(jobEId, DAGJobType.TIME), null);
        jobGraph.addJob(jobFId, new DAGJob(jobFId, DAGJobType.DEPEND), Sets.newHashSet(jobDId, jobEId));
        NextDayPlanGenerator planGenerator = new NextDayPlanGenerator();
        planGenerator.generateNextPlan();
    }

    @Test
    public void testGetReschedulePlan() throws JobScheduleException {
        jobGraph.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        jobGraph.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        jobGraph.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        jobGraph.addJob(jobDId, new DAGJob(jobDId, DAGJobType.TIME), null);
        jobGraph.addJob(jobEId, new DAGJob(jobEId, DAGJobType.TIME), null);
        jobGraph.addJob(jobFId, new DAGJob(jobFId, DAGJobType.DEPEND), Sets.newHashSet(jobDId, jobEId));

        PlanGenerator planGererator = new PlanGenerator();
        Range<DateTime> range = Range.closed(new DateTime("2015-10-10"), new DateTime("2015-10-12"));
        List<ExecutionPlanEntry> planList = planGererator.getReschedulePlan(jobAId, range);
        System.out.println(planList.size());
        for (ExecutionPlanEntry planEntry : planList) {
            System.out.println(planEntry.getJobId() + "," + planEntry.getDateTime());
        }

        planList = planGererator.getReschedulePlan(jobFId, range);
        System.out.println(planList.size());
        for (ExecutionPlanEntry planEntry : planList) {
            System.out.println(planEntry.getJobId() + "," + planEntry.getDateTime());
        }
    }
}
