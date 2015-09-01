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
import com.mogujie.jarvis.server.scheduler.JobDescriptor;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.dag.event.DAGEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.TimeReadyEvent;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJobFactory;
import com.mogujie.jarvis.server.scheduler.dag.job.TimeDAGJob;
import com.mogujie.jarvis.server.scheduler.dag.status.IJobDependStatus;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;

/**
 * Scheduler used to handle dependency based job.
 *
 * @author guangming
 *
 */
public enum DAGScheduler {
    INSTANCE;

    TaskScheduler taskScheduler = TaskScheduler.INSTANCE;
    private Configuration conf = ConfigUtils.getServerConfig();
    private Map<Long, DAGJob> waitingTable = new ConcurrentHashMap<Long, DAGJob>();

    public void init() {
        // TODO Auto-generated method stub
        // 1. load all job
    }

    public void run() {
        // TODO Auto-generated method stub

    }

    public void stop() {
        // TODO Auto-generated method stub

    }

    /**
     * add job
     *
     * @param JobDescriptor jobDesc
     */
    public void addJob(JobDescriptor jobDesc) throws Exception {
        long jobid = jobDesc.getJobContext().getJobId();
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
     * @param JobDescriptor jobDesc
     */
    public void removeJob(JobDescriptor jobDesc) {
        long jobid = jobDesc.getJobContext().getJobId();
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
     * @param long parentId
     * @param long childId
     */
    public void addDependency(long parentId, long childId) {
        DAGJob parent = waitingTable.get(parentId);
        DAGJob child = waitingTable.get(childId);
        if (parent != null && child != null) {
            parent.addChild(child);
            child.addParent(parent);
        }
    }

    /**
     * remove dependency
     *
     * @param long parentId
     * @param long childId
     */
    public void removeDependency(long parentId, long childId) {
        DAGJob parent = waitingTable.get(parentId);
        DAGJob child = waitingTable.get(childId);
        if (parent != null && child != null) {
            parent.removeChild(child);
            child.removeParent(parent);
        }
    }

    /**
     * get dependent parent
     *
     * @param long jobid
     * @return parent jobid list
     */
    public List<Long> getParents(long jobid) {
        List<Long> parentIds = new ArrayList<Long>();
        DAGJob dagJob = waitingTable.get(jobid);
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
     * @param long jobid
     * @return children jobid list
     */
    public List<Long> getChildren(long jobid) {
        List<Long> childIds = new ArrayList<Long>();
        DAGJob dagJob = waitingTable.get(jobid);
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

    public void handleEvent(DAGEvent e) throws DAGScheduleException {
        if (e instanceof TimeReadyEvent) {
            handleTimeReadyEvent((TimeReadyEvent)e);
        } else if (e instanceof SuccessEvent) {
            handleSuccessEvent((SuccessEvent)e);
        }

    }

    private void handleTimeReadyEvent(TimeReadyEvent e) throws DAGScheduleException {
        long jobid = e.getJobid();
        DAGJob dagJob = waitingTable.get(jobid);
        if (!(dagJob instanceof TimeDAGJob)) {
            throw new DAGScheduleException("Job schedule type error. jobid "
                    + e.getJobid() +  " is not TimeDAGJob");
        }
        TimeDAGJob tDagJob = ((TimeDAGJob)dagJob);
        tDagJob.timeReady();

        if (tDagJob.dependCheck()) {
            taskScheduler.submitJob(SchedulerUtil.getJobContext(jobid));
        }
    }

    private void handleSuccessEvent(SuccessEvent e) throws DAGScheduleException {

    }
}
