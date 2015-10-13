/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月8日 下午8:03:32
 */
package com.mogujie.jarvis.server.scheduler.dag;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.server.util.SpringContext;


/**
 * @author guangming
 *
 */
public class TestDAGScheduler {

    private DAGJob jobA;
    private DAGJob jobB;
    private DAGJob jobC;
    private static DAGScheduler scheduler;

    @BeforeClass
    public static void setup() throws Exception {
        scheduler = SpringContext.getBean(DAGScheduler.class);
        scheduler.clear();
    }

    @After
    public void tearDown() throws Exception {
        scheduler.clear();
    }

    /**
     *   A   B
     *    \ /
     *     C
     * @throws CycleFoundException
     */
    @Test
    public void testAddJob1() throws Exception {
        jobA = new DAGJob(1, DAGJobType.TIME);
        jobB = new DAGJob(2, DAGJobType.TIME);
        jobC = new DAGJob(3, DAGJobType.DEPEND);
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, null);
        scheduler.addJob(jobC.getJobId(), jobC, Sets.newHashSet(jobA.getJobId(),jobB.getJobId()));
        Assert.assertEquals(1, scheduler.getChildren(jobA.getJobId()).size());
        Assert.assertEquals(3, (long)scheduler.getChildren(jobA.getJobId()).get(0).getFirst());
        Assert.assertEquals(1, scheduler.getChildren(jobB.getJobId()).size());
        Assert.assertEquals(3, (long)scheduler.getChildren(jobB.getJobId()).get(0).getFirst());
        Assert.assertEquals(2, scheduler.getParents(jobC.getJobId()).size());
    }

    /**
     *     A
     *    / \
     *   B   C
     * @throws CycleFoundException
     */
    @Test
    public void testAddJob2() throws Exception {
        jobA = new DAGJob(1, DAGJobType.TIME);
        jobB = new DAGJob(2, DAGJobType.DEPEND);
        jobC = new DAGJob(3, DAGJobType.DEPEND);
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, Sets.newHashSet(jobA.getJobId()));
        scheduler.addJob(jobC.getJobId(), jobC, Sets.newHashSet(jobA.getJobId()));
        Assert.assertEquals(2, scheduler.getChildren(jobA.getJobId()).size());
        Assert.assertEquals(1, scheduler.getParents(jobB.getJobId()).size());
        Assert.assertEquals(1, (long)scheduler.getParents(jobB.getJobId()).get(0).getFirst());
        Assert.assertEquals(1, scheduler.getParents(jobC.getJobId()).size());
        Assert.assertEquals(1, (long)scheduler.getParents(jobC.getJobId()).get(0).getFirst());
    }

    /**
     *   A   B        A
     *    \ /   -->   |
     *     C          C
     * @throws CycleFoundException
     */
    @Test
    public void testRemoveJob1() throws Exception {
        jobA = new DAGJob(1, DAGJobType.TIME);
        jobB = new DAGJob(2, DAGJobType.TIME);
        jobC = new DAGJob(3, DAGJobType.DEPEND);
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, null);
        scheduler.addJob(jobC.getJobId(), jobC, Sets.newHashSet(jobA.getJobId(),jobB.getJobId()));
        Assert.assertEquals(1, scheduler.getChildren(jobA.getJobId()).size());
        Assert.assertEquals(3, (long)scheduler.getChildren(jobA.getJobId()).get(0).getFirst());
        Assert.assertEquals(1, scheduler.getChildren(jobB.getJobId()).size());
        Assert.assertEquals(3, (long)scheduler.getChildren(jobB.getJobId()).get(0).getFirst());
        Assert.assertEquals(2, scheduler.getParents(jobC.getJobId()).size());

        scheduler.removeJob(jobB);
        Assert.assertEquals(1, scheduler.getChildren(jobA.getJobId()).size());
        Assert.assertEquals(3, (long)scheduler.getChildren(jobA.getJobId()).get(0).getFirst());
        Assert.assertEquals(1, scheduler.getParents(jobC.getJobId()).size());
        Assert.assertEquals(1, (long)scheduler.getParents(jobC.getJobId()).get(0).getFirst());
    }

    /**
     *   A   B
     *    \ /   -->  A  B
     *     C
     * @throws CycleFoundException
     */
    @Test
    public void testRemoveJob2() throws Exception {
        jobA = new DAGJob(1, DAGJobType.TIME);
        jobB = new DAGJob(2, DAGJobType.TIME);
        jobC = new DAGJob(3, DAGJobType.DEPEND);
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, null);
        scheduler.addJob(jobC.getJobId(), jobC, Sets.newHashSet(jobA.getJobId(),jobB.getJobId()));
        Assert.assertEquals(1, scheduler.getChildren(jobA.getJobId()).size());
        Assert.assertEquals(3, (long)scheduler.getChildren(jobA.getJobId()).get(0).getFirst());
        Assert.assertEquals(1, scheduler.getChildren(jobB.getJobId()).size());
        Assert.assertEquals(3, (long)scheduler.getChildren(jobB.getJobId()).get(0).getFirst());
        Assert.assertEquals(2, scheduler.getParents(jobC.getJobId()).size());

        scheduler.removeJob(jobC);
        Assert.assertEquals(0, scheduler.getChildren(jobA.getJobId()).size());
        Assert.assertEquals(0, scheduler.getChildren(jobB.getJobId()).size());
        Assert.assertEquals(0, scheduler.getParents(jobC.getJobId()).size());
    }

    /**
     *     A
     *    / \   -->  B  C
     *   B   C
     * @throws CycleFoundException
     */
    @Test
    public void testRemoveJob3() throws Exception {
        jobA = new DAGJob(1, DAGJobType.TIME);
        jobB = new DAGJob(2, DAGJobType.DEPEND);
        jobC = new DAGJob(3, DAGJobType.DEPEND);
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, Sets.newHashSet(jobA.getJobId()));
        scheduler.addJob(jobC.getJobId(), jobC, Sets.newHashSet(jobA.getJobId()));
        Assert.assertEquals(2, scheduler.getChildren(jobA.getJobId()).size());
        Assert.assertEquals(1, scheduler.getParents(jobB.getJobId()).size());
        Assert.assertEquals(1, (long)scheduler.getParents(jobB.getJobId()).get(0).getFirst());
        Assert.assertEquals(1, scheduler.getParents(jobC.getJobId()).size());
        Assert.assertEquals(1, (long)scheduler.getParents(jobC.getJobId()).get(0).getFirst());

        scheduler.removeJob(jobA);
        Assert.assertEquals(0, scheduler.getChildren(jobA.getJobId()).size());
        Assert.assertEquals(0, scheduler.getChildren(jobB.getJobId()).size());
        Assert.assertEquals(0, scheduler.getChildren(jobC.getJobId()).size());
    }

    /**
     *     A
     *     |           A
     *     B   -->    / \
     *     |         B   C
     *     C
     * @throws CycleFoundException
     */
    @Test
    public void testModifyDependency() throws Exception {
        jobA = new DAGJob(1, DAGJobType.TIME);
        jobB = new DAGJob(2, DAGJobType.DEPEND);
        jobC = new DAGJob(3, DAGJobType.DEPEND);
        scheduler.addJob(jobA.getJobId(), jobA, null);
        scheduler.addJob(jobB.getJobId(), jobB, null);
        scheduler.addJob(jobC.getJobId(), jobC, null);

        scheduler.addDependency(jobA.getJobId(), jobB.getJobId());
        scheduler.addDependency(jobB.getJobId(), jobC.getJobId());
        Assert.assertEquals(1, scheduler.getChildren(jobA.getJobId()).size());
        Assert.assertEquals(jobB.getJobId(), (long)scheduler.getChildren(jobA.getJobId()).get(0).getFirst());
        Assert.assertEquals(1, scheduler.getParents(jobB.getJobId()).size());
        Assert.assertEquals(jobA.getJobId(), (long)scheduler.getParents(jobB.getJobId()).get(0).getFirst());
        Assert.assertEquals(1, scheduler.getChildren(jobB.getJobId()).size());
        Assert.assertEquals(jobC.getJobId(), (long)scheduler.getChildren(jobB.getJobId()).get(0).getFirst());

        scheduler.removeDependency(jobB.getJobId(), jobC.getJobId());
        scheduler.addDependency(jobA.getJobId(), jobC.getJobId());
        Assert.assertEquals(2, scheduler.getChildren(jobA.getJobId()).size());
    }

}
