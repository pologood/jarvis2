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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.server.scheduler.InitEvent;
import com.mogujie.jarvis.server.scheduler.JobScheduleType;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.StartEvent;
import com.mogujie.jarvis.server.scheduler.StopEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.AddJobEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.ModifyJobEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.ModifyJobEvent.MODIFY_TYPE;
import com.mogujie.jarvis.server.scheduler.dag.event.RemoveJobEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.TimeReadyEvent;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGJobFactory;
import com.mogujie.jarvis.server.scheduler.dag.status.AbstractDependStatus;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.service.CrontabService;
import com.mogujie.jarvis.server.service.JobDependService;
import com.mogujie.jarvis.server.service.JobService;

/**
 * Scheduler used to handle dependency based job.
 *
 * @author guangming
 *
 */
@Service
public class DAGScheduler implements Scheduler {
    @Autowired
    JobService jobService;

    @Autowired
    JobDependService jobDependService;

    @Autowired
    CrontabService cronService;

    @Autowired
    JobMapper jobMapper;

    // for testing
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
        // load job from DB
        List<Job> jobs = jobService.loadJobs();
        for (Job job : jobs) {
            long jobId = job.getJobId();
            Set<Long> dependencies = jobDependService.getDependIds(jobId);
            boolean hasCron = (!cronService.getCronIds(jobId).isEmpty());
            boolean hasDepend = (!dependencies.isEmpty());
            JobScheduleType type = SchedulerUtil.getJobScheduleType(hasCron, hasDepend);
            JobDependencyStrategy strategy = JobDependencyStrategy.ALL;
            AddJobEvent addJobEvent = new AddJobEvent(jobId, dependencies, type, strategy);
            try {
                handleAddJobEvent(addJobEvent);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public void handleStartEvent(StartEvent event) {

    }

    @Override
    public void handleStopEvent(StopEvent event) {

    }

    /**
     * add job
     *
     * @param JobDescriptor jobDesc
     */
    @Subscribe
    public void handleAddJobEvent(AddJobEvent event) throws Exception {
        long jobId = event.getJobId();
        if (waitingTable.get(jobId) == null) {
            AbstractDependStatus jobDependStatus = SchedulerUtil.getJobDependStatus(conf);
            if (jobDependStatus != null) {
                jobDependStatus.setMyjobId(jobId);
                JobScheduleType scheduleType = event.getScheduleType();
                DAGJob dagJob = DAGJobFactory.createDAGJob(scheduleType, jobId,
                        jobDependStatus, JobDependencyStrategy.ALL);
                addJob(jobId, dagJob, event.getDependencies());
            }
        }
    }

    @VisibleForTesting
    protected void addJob(long jobId, DAGJob dagJob, Set<Long> dependencies) {
        waitingTable.put(jobId, dagJob);

        if (dependencies != null) {
            for (long d: dependencies) {
                DAGJob parent = waitingTable.get(d);
                if (parent != null) {
                    dagJob.addParent(parent);
                    parent.addChild(dagJob);
                }
            }
        }
    }

    @VisibleForTesting
    protected void clear() {
        waitingTable.clear();
    }

    /**
     * remove job
     *
     * @param long jobId
     */
    @Subscribe
    public void handleRemoveJobEvent(RemoveJobEvent event) {
        long jobId = event.getJobId();
        removeJob(jobId);
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            // submit job if pass dependency check
            List<DAGJob> children = dagJob.getChildren();
            for (DAGJob child : children) {
               submitJobWithCheck(child);
            }
        }
    }

    @VisibleForTesting
    protected void removeJob(long jobId) {
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            // 1. remove job from waiting table
            waitingTable.remove(dagJob);
            // 2. remove relation from parents
            dagJob.removeParents();
            // 3. remove relation from children
            dagJob.removeChildren();
        }
    }

    @Subscribe
    public void handleModifyJobEvent(ModifyJobEvent event) {
        MODIFY_TYPE modifyType = event.getModifyType();
        long jobId = event.getJobId();
        Set<Long> dependencies = event.getDependencies();
        // modify dependency
        if (modifyType.equals(MODIFY_TYPE.ADD)) {
            for (long d : dependencies) {
                addDependency(d, jobId);
            }
        } else if (modifyType.equals(MODIFY_TYPE.DEL)) {
            for (long d : dependencies) {
                removeDependency(d, jobId);
            }
            DAGJob dagJob = waitingTable.get(jobId);
            if (dagJob != null) {
                submitJobWithCheck(dagJob);
            }
        } else if (modifyType.equals(MODIFY_TYPE.MODIFY)) {
            DAGJob dagJob = waitingTable.get(jobId);
            if (dagJob != null) {
                dagJob.removeParents(false);
                for (long d : dependencies) {
                    addDependency(d, jobId);
                }
            }
        }

        // modify time ready flag
        boolean hasCron = event.isHasCron();
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            dagJob.setHasTimeFlag(hasCron);
            submitJobWithCheck(dagJob);
        }
    }

    @VisibleForTesting
    public void addDependency(long parentId, long childId) {
        DAGJob parent = waitingTable.get(parentId);
        DAGJob child = waitingTable.get(childId);
        if (parent != null && child != null) {
            parent.addChild(child);
            child.addParent(parent);
        }
    }

    @VisibleForTesting
    protected void removeDependency(long parentId, long childId) {
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
     * @param long jobId
     * @return parent jobId list
     */
    public List<Long> getParents(long jobId) {
        List<Long> parentIds = new ArrayList<Long>();
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            List<DAGJob> parentJobs = dagJob.getParents();
            if (parentJobs != null) {
                for (DAGJob p : parentJobs) {
                    parentIds.add(p.getJobId());
                }
            }
        }

        return parentIds;
    }

    /**
     * get subsequent child
     *
     * @param long jobId
     * @return children jobId list
     */
    public List<Long> getChildren(long jobId) {
        List<Long> childIds = new ArrayList<Long>();
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            List<DAGJob> childJobs = dagJob.getChildren();
            if (childJobs != null) {
                for (DAGJob c : childJobs) {
                    childIds.add(c.getJobId());
                }
            }
        }

        return childIds;
    }

    @Subscribe
    public void handleTimeReadyEvent(TimeReadyEvent e) throws DAGScheduleException {
        long jobId = e.getJobId();
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            if (!dagJob.isHasTimeFlag()) {
                throw new DAGScheduleException("No time ready flag. jobId is " + e.getJobId());
            }
            // 更新时间标识
            dagJob.setTimeReadyFlag();
            // 如果通过依赖检查，提交给taskScheduler，并重置自己的依赖状态
            submitJobWithCheck(dagJob);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleSuccessEvent(SuccessEvent e) throws DAGScheduleException {
        long jobId = e.getJobId();
        long taskId = e.getTaskId();
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            List<DAGJob> children = dagJob.getChildren();
            if (children != null) {
                for (DAGJob child : children) {
                    // 更新依赖状态为true
                    child.setDependStatus(jobId, taskId);
                    // 如果通过依赖检查，提交给taskScheduler，并重置自己的依赖状态
                    submitJobWithCheck(child);
                }
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleFailedEvent(FailedEvent e) throws DAGScheduleException {
        long jobId = e.getJobId();
        long taskId = e.getTaskId();
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            List<DAGJob> children = dagJob.getChildren();
            if (children != null) {
                for (DAGJob child : children) {
                    // 更新依赖状态为false
                    child.resetDependStatus(jobId, taskId);
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
            taskScheduler.submitJob(dagJob.getJobId());
            dagJob.resetDependStatus();
        }
    }
}
