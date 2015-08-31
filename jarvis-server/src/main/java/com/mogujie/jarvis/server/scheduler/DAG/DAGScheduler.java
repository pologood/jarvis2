/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:07
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.server.scheduler.AbstractScheduler;
import com.mogujie.jarvis.server.scheduler.JobDescriptor;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJobFactory;
import com.mogujie.jarvis.server.scheduler.dag.status.IJobDependStatus;

/**
 * Scheduler used to handle dependency based job.
 *
 * @author guangming
 *
 */
public class DAGScheduler extends AbstractScheduler {

    private Configuration conf = ConfigUtils.getServerConfig();
    private Map<Integer, DAGJob> waitingTable = new ConcurrentHashMap<Integer, DAGJob>();

    @Override
    public void init() {
        // TODO Auto-generated method stub
        // 1. load all job
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    public void addJob(JobDescriptor jobDesc) throws Exception {
        int jobid = (int) jobDesc.getJobContext().getJobId();
        if (waitingTable.get(jobid) == null) {
            IJobDependStatus jobDependStatus = SchedulerUtil.getJobDependStatus(conf);
            if (jobDependStatus != null) {
                DAGJob dagJob = DAGJobFactory.createDAGJob(jobDesc.getScheduleType(),
                        jobid, jobDependStatus, JobDependencyStrategy.ALL);
                waitingTable.put(jobid, dagJob);

                long[] dependencies = jobDesc.getJobContext().getDependencyJobids();
                for (long d: dependencies) {
                    DAGJob parent = waitingTable.get(d);
                    if (parent != null) {
                        dagJob.addParent(parent);
                        parent.addChild(dagJob);
                    }
                }
            }
        }
    }

    public void removeJob(JobDescriptor jobDesc) {

    }

    public void addDependency(JobDescriptor parent, JobDescriptor child) {

    }

    public void removeDependency(JobDescriptor parent, JobDescriptor child) {

    }

}
