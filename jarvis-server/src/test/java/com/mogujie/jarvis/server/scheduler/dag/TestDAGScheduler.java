/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月8日 下午8:03:32
 */
package com.mogujie.jarvis.server.scheduler.dag;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.server.scheduler.JobScheduleType;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJobFactory;


/**
 * @author guangming
 *
 */
public class TestDAGScheduler {

    private DAGJob jobA;
    private DAGJob jobB;
    private DAGJob jobC;
    private DAGScheduler scheduler = DAGScheduler.getInstance();

    @Before
    public void setup() throws Exception {
        jobA = DAGJobFactory.createDAGJob(JobScheduleType.CRONTAB, 1);
        jobB = DAGJobFactory.createDAGJob(JobScheduleType.DEPENDENCY, 2);
        jobC = DAGJobFactory.createDAGJob(JobScheduleType.DEPENDENCY, 3);
    }

    @After
    public void tearDown() throws Exception {
        scheduler.clear();
    }

    /**
     *   A   B
     *    \ /
     *     C
     */
    @Test
    public void testAddJob1() {
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, null);
        scheduler.addJob(jobC.getJobId(), jobC, Sets.newHashSet(jobA.getJobId(),jobB.getJobId()));
        Assert.assertEquals(1, jobA.getChildren().size());
        Assert.assertEquals(3, jobA.getChildren().get(0).getJobId());
        Assert.assertEquals(1, jobB.getChildren().size());
        Assert.assertEquals(3, jobB.getChildren().get(0).getJobId());
        Assert.assertEquals(2, jobC.getParents().size());
    }

    /**
     *     A
     *    / \
     *   B   C
     */
    @Test
    public void testAddJob2() {
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, Sets.newHashSet(jobA.getJobId()));
        scheduler.addJob(jobC.getJobId(), jobC, Sets.newHashSet(jobA.getJobId()));
        Assert.assertEquals(2, jobA.getChildren().size());
        Assert.assertEquals(1, jobB.getParents().size());
        Assert.assertEquals(1, jobB.getParents().get(0).getJobId());
        Assert.assertEquals(1, jobC.getParents().size());
        Assert.assertEquals(1, jobC.getParents().get(0).getJobId());
    }

    /**
     *   A   B        A
     *    \ /   -->   |
     *     C          C
     */
    @Test
    public void testRemoveJob1() {
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, null);
        scheduler.addJob(jobC.getJobId(), jobC, Sets.newHashSet(jobA.getJobId(),jobB.getJobId()));
        Assert.assertEquals(1, jobA.getChildren().size());
        Assert.assertEquals(3, jobA.getChildren().get(0).getJobId());
        Assert.assertEquals(1, jobB.getChildren().size());
        Assert.assertEquals(3, jobB.getChildren().get(0).getJobId());
        Assert.assertEquals(2, jobC.getParents().size());

        scheduler.removeJob(jobB);
        Assert.assertEquals(1, jobA.getChildren().size());
        Assert.assertEquals(3, jobA.getChildren().get(0).getJobId());
        Assert.assertEquals(1, jobC.getParents().size());
        Assert.assertEquals(1, jobC.getParents().get(0).getJobId());
    }

    /**
     *   A   B
     *    \ /   -->  A  B
     *     C
     */
    @Test
    public void testRemoveJob2() {
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, null);
        scheduler.addJob(jobC.getJobId(), jobC, Sets.newHashSet(jobA.getJobId(),jobB.getJobId()));
        Assert.assertEquals(1, jobA.getChildren().size());
        Assert.assertEquals(3, jobA.getChildren().get(0).getJobId());
        Assert.assertEquals(1, jobB.getChildren().size());
        Assert.assertEquals(3, jobB.getChildren().get(0).getJobId());
        Assert.assertEquals(2, jobC.getParents().size());

        scheduler.removeJob(jobC);
        Assert.assertEquals(0, jobA.getChildren().size());
        Assert.assertEquals(0, jobB.getChildren().size());
        Assert.assertEquals(0, jobC.getParents().size());
    }

    /**
     *     A
     *    / \   -->  B  C
     *   B   C
     */
    @Test
    public void testRemoveJob3() {
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, Sets.newHashSet(jobA.getJobId()));
        scheduler.addJob(jobC.getJobId(), jobC, Sets.newHashSet(jobA.getJobId()));
        Assert.assertEquals(2, jobA.getChildren().size());
        Assert.assertEquals(1, jobB.getParents().size());
        Assert.assertEquals(1, jobB.getParents().get(0).getJobId());
        Assert.assertEquals(1, jobC.getParents().size());
        Assert.assertEquals(1, jobC.getParents().get(0).getJobId());

        scheduler.removeJob(jobA);
        Assert.assertEquals(0, jobA.getChildren().size());
        Assert.assertEquals(0, jobB.getParents().size());
        Assert.assertEquals(0, jobC.getParents().size());
    }

    /**
     *     A
     *     |           A
     *     B   -->    / \
     *     |         B   C
     *     C
     */
    @Test
    public void testModifyDependency() {
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, null);
        scheduler.addJob(jobC.getJobId(), jobC, null);

        scheduler.addDependency(jobA.getJobId(), jobB.getJobId());
        scheduler.addDependency(jobB.getJobId(), jobC.getJobId());
        Assert.assertEquals(1, jobA.getChildren().size());
        Assert.assertEquals(jobB.getJobId(), jobA.getChildren().get(0).getJobId());
        Assert.assertEquals(1, jobB.getParents().size());
        Assert.assertEquals(jobA.getJobId(), jobB.getParents().get(0).getJobId());
        Assert.assertEquals(1, jobB.getChildren().size());
        Assert.assertEquals(jobC.getJobId(), jobB.getChildren().get(0).getJobId());

        scheduler.removeDependency(jobB.getJobId(), jobC.getJobId());
        scheduler.addDependency(jobA.getJobId(), jobC.getJobId());
        Assert.assertEquals(2, jobA.getChildren().size());
    }

}
