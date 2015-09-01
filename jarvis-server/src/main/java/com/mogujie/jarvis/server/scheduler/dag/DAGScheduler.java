/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:07
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * add job
     *
     * @param jobDesc
     */
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

    /**
     * remove job
     *
     * @param jobDesc
     */
    public void removeJob(JobDescriptor jobDesc) {
        int jobid = (int) jobDesc.getJobContext().getJobId();
        DAGJob dagJob = waitingTable.get(jobid);
        if (dagJob != null) {
            // 1. remove job from waiting table
            waitingTable.remove(dagJob);

            // 2. remove relation from parents
            List<DAGJob> parents = dagJob.getParents();
            for (DAGJob p : parents) {
                p.removeChild(dagJob);
                dagJob.removeParent(p);
            }

            // 3. remove relation from children
            List<DAGJob> children = dagJob.getChildren();
            for (DAGJob c : children) {
               dagJob.removeChild(c);
               c.removeParent(dagJob);
            }
        }
    }

    /**
     * add dependency
     *
     * @param jobDesc parent
     * @param jobDesc child
     */
    public void addDependency(JobDescriptor jp, JobDescriptor jc) {
        DAGJob parent = waitingTable.get(jp.getJobContext().getJobId());
        DAGJob child = waitingTable.get(jc.getJobContext().getJobId());
        if (parent != null && child != null) {
            parent.addChild(child);
            child.addParent(parent);
        }
    }

    /**
     * remove dependency
     *
     * @param jobDesc parent
     * @param jobDesc child
     */
    public void removeDependency(JobDescriptor jp, JobDescriptor jc) {
        DAGJob parent = waitingTable.get(jp.getJobContext().getJobId());
        DAGJob child = waitingTable.get(jc.getJobContext().getJobId());
        if (parent != null && child != null) {
            parent.removeChild(child);
            child.removeParent(parent);
        }
    }

    /**
     * get dependency parent
     *
     * @param jobDesc
     */
    public List<Integer> getParents(JobDescriptor jobDesc) {
        List<Integer> parentIds = new ArrayList<Integer>();
        DAGJob dagJob = waitingTable.get(jobDesc.getJobContext().getJobId());
        if (dagJob != null) {
            List<DAGJob> parentJobs = dagJob.getParents();
            if (parentJobs != null) {
                for (DAGJob p : parentJobs) {
                    parentIds.add(p.getJobid());
                }
            }
        }

        return parentIds;
    }

    /**
     * get subsequent child
     *
     * @param jobDesc
     */
    public List<Integer> getChildren(JobDescriptor jobDesc) {
        List<Integer> childIds = new ArrayList<Integer>();
        DAGJob dagJob = waitingTable.get(jobDesc.getJobContext().getJobId());
        if (dagJob != null) {
            List<DAGJob> childJobs = dagJob.getParents();
            if (childJobs != null) {
                for (DAGJob c : childJobs) {
                    childIds.add(c.getJobid());
                }
            }
        }

        return childIds;
    }

}
