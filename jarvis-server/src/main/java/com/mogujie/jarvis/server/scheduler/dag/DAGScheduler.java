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

import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.server.observer.InitEvent;
import com.mogujie.jarvis.server.observer.StopEvent;
import com.mogujie.jarvis.server.scheduler.JobDescriptor;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.dag.event.AddJobEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.RemoveJobEvent;
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
public class DAGScheduler implements Scheduler {

    private static DAGScheduler instance = new DAGScheduler();
    private DAGScheduler() {}
    public static DAGScheduler getInstance() {
        return instance;
    }

    private TaskScheduler taskScheduler = TaskScheduler.getInstance();
    private Configuration conf = ConfigUtils.getServerConfig();
    private Map<Long, DAGJob> waitingTable = new ConcurrentHashMap<Long, DAGJob>();

    @Override
    public void handleInitEvent(InitEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleStopEvent(StopEvent event) {
        // TODO Auto-generated method stub

    }

    /**
     * add job
     *
     * @param JobDescriptor jobDesc
     */
    @Subscribe
    public void handleAddJobEvent(AddJobEvent event) throws Exception {
        JobDescriptor jobDesc = event.getJobDesc();
        long jobid = jobDesc.getJobContext().getJobId();
        if (waitingTable.get(jobid) == null) {
            IJobDependStatus jobDependStatus = SchedulerUtil.getJobDependStatus(conf);
            if (jobDependStatus != null) {
                jobDependStatus.setMyjobid(jobid);
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
     * @param long jobid
     */
    @Subscribe
    public void handleRemoveJobEvent(RemoveJobEvent event) {
        long jobid = event.getJobid();
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
            for (DAGJob child : children) {
               dagJob.removeChild(child);
               // 1. remove relation from parent
               child.removeParent(dagJob);
               // 2. remove dependency status with jobid
               child.removeDenpendency(jobid);
               // 3. submit job if pass dependency check
               submitJobWithCheck(child);
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
            child.removeDenpendency(parent.getJobid());
            submitJobWithCheck(child);
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

    @Subscribe
    public void handleTimeReadyEvent(TimeReadyEvent e) throws DAGScheduleException {
        long jobid = e.getJobid();
        DAGJob dagJob = waitingTable.get(jobid);
        if (dagJob != null) {
            if (!(dagJob instanceof TimeDAGJob)) {
                throw new DAGScheduleException("Job schedule type error. jobid "
                        + e.getJobid() +  " is not TimeDAGJob");
            }
            // 更新时间标识
            TimeDAGJob tDagJob = ((TimeDAGJob)dagJob);
            tDagJob.timeReady();

            // 如果通过依赖检查，提交给taskScheduler，并重置自己的依赖状态
            submitJobWithCheck(tDagJob);
        }
    }

    @Subscribe
    public void handleSuccessEvent(SuccessEvent e) throws DAGScheduleException {
        long jobid = e.getJobid();
        long taskid = e.getTaskid();
        DAGJob dagJob = waitingTable.get(jobid);
        if (dagJob != null) {
            List<DAGJob> children = dagJob.getChildren();
            if (children != null) {
                for (DAGJob child : children) {
                    // 更新依赖状态
                    child.addReadyDependency(jobid, taskid);
                    // 如果通过依赖检查，提交给taskScheduler，并重置自己的依赖状态
                    submitJobWithCheck(child);
                }
            }
        }
    }

    /**
     * submit job if pass the dependency check
     *
     * @param DAGJob dagJob
     */
    private void submitJobWithCheck(DAGJob dagJob) {
        if (dagJob.dependCheck()) {
            taskScheduler.submitJob(dagJob.getJobid());
            dagJob.resetDependStatus();
        }
    }
}
