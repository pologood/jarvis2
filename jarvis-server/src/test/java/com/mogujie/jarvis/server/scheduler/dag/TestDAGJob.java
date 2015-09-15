/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月15日 上午10:37:22
 */

package com.mogujie.jarvis.server.scheduler.dag;

import org.junit.Assert;
import org.junit.Test;

import com.mogujie.jarvis.server.scheduler.JobScheduleType;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJobFactory;
import com.mogujie.jarvis.server.scheduler.dag.status.CachedDependStatus;

/**
 * @author guangming
 *
 */
public class TestDAGJob {
    private long jobAId = 1;
    private long jobBId = 2;
    private long jobCId = 3;
    private long jobDId = 4;
    private long jobEId = 5;

    /**
     *      A
     *      |
     *      B
     *     / \
     *    C   D
     *     \ /
     *      E
     */
    @Test
    public void testJob1() throws Exception {
        DAGJob jobA = DAGJobFactory.createDAGJob(JobScheduleType.CRONTAB, jobAId,
                new CachedDependStatus(), JobDependencyStrategy.ALL);
        DAGJob jobB = DAGJobFactory.createDAGJob(JobScheduleType.CRONTAB, jobBId,
                new CachedDependStatus(), JobDependencyStrategy.ALL);
        DAGJob jobC = DAGJobFactory.createDAGJob(JobScheduleType.CRONTAB, jobCId,
                new CachedDependStatus(), JobDependencyStrategy.ALL);
        DAGJob jobD = DAGJobFactory.createDAGJob(JobScheduleType.CRONTAB, jobDId,
                new CachedDependStatus(), JobDependencyStrategy.ALL);
        DAGJob jobE = DAGJobFactory.createDAGJob(JobScheduleType.CRONTAB, jobEId,
                new CachedDependStatus(), JobDependencyStrategy.ALL);
        jobA.addChild(jobB);
        jobB.addParent(jobA);
        jobB.addChild(jobC);
        jobC.addParent(jobB);
        jobB.addChild(jobD);
        jobD.addParent(jobB);
        jobC.addChild(jobE);
        jobE.addParent(jobC);
        jobD.addChild(jobE);
        jobE.addParent(jobD);

        Assert.assertEquals(1, jobA.getChildren().size());
        Assert.assertEquals(jobBId, jobA.getChildren().get(0).getJobId());
        Assert.assertEquals(1, jobB.getParents().size());
        Assert.assertEquals(jobAId, jobB.getParents().get(0).getJobId());
        Assert.assertEquals(2, jobB.getChildren().size());
        Assert.assertEquals(1, jobC.getParents().size());
        Assert.assertEquals(jobBId, jobC.getParents().get(0).getJobId());
        Assert.assertEquals(1, jobC.getChildren().size());
        Assert.assertEquals(jobEId, jobC.getChildren().get(0).getJobId());
        Assert.assertEquals(1, jobD.getParents().size());
        Assert.assertEquals(jobBId, jobD.getParents().get(0).getJobId());
        Assert.assertEquals(1, jobD.getChildren().size());
        Assert.assertEquals(jobEId, jobD.getChildren().get(0).getJobId());
        Assert.assertEquals(2, jobE.getParents().size());

        jobA.removeChild(jobB);
        Assert.assertEquals(0, jobA.getChildren().size());
        Assert.assertEquals(1, jobB.getParents().size());
        jobB.removeParent(jobA);
        Assert.assertEquals(0, jobB.getParents().size());

        jobB.removeChildren();
        Assert.assertEquals(0, jobB.getChildren().size());
        Assert.assertEquals(0, jobC.getParents().size());
        Assert.assertEquals(0, jobD.getParents().size());

        jobE.removeParents();
        Assert.assertEquals(0, jobC.getChildren().size());
        Assert.assertEquals(0, jobD.getChildren().size());
        Assert.assertEquals(0, jobE.getParents().size());
    }

    @Test
    public void testJob2() throws Exception {
        DAGJob jobA = DAGJobFactory.createDAGJob(JobScheduleType.CRONTAB, jobAId,
                new CachedDependStatus(), JobDependencyStrategy.ALL);
        DAGJob jobB = DAGJobFactory.createDAGJob(JobScheduleType.CRONTAB, jobBId,
                new CachedDependStatus(), JobDependencyStrategy.ALL);
        DAGJob jobC = DAGJobFactory.createDAGJob(JobScheduleType.CRONTAB, jobCId,
                new CachedDependStatus(), JobDependencyStrategy.ALL);
        jobA.addChild(jobB);
        jobA.addChild(jobC);
        Assert.assertEquals(2, jobA.getChildren().size());
        jobA.addChild(jobB);
        jobA.addChild(jobC);
        Assert.assertEquals(2, jobA.getChildren().size());

        jobA.removeChild(jobB);
        Assert.assertEquals(1, jobA.getChildren().size());
        jobA.removeChild(jobB);
        jobA.removeChild(jobB);
        Assert.assertEquals(1, jobA.getChildren().size());
        jobA.removeChild(jobC);
        Assert.assertEquals(0, jobA.getChildren().size());
    }
}
