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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.event.AddJobEvent;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobEvent.MODIFY_TYPE;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobFlagEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;
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
@Repository
public class DAGScheduler extends Scheduler {
    @Autowired
    private JobService jobService;

    @Autowired
    private JobDependService jobDependService;

    @Autowired
    private CrontabService cronService;

    private static DAGScheduler instance = new DAGScheduler();
    private DAGScheduler() {}
    public static DAGScheduler getInstance() {
        return instance;
    }

    private TaskScheduler taskScheduler = TaskScheduler.getInstance();
    private Map<Long, DAGJob> waitingTable = new ConcurrentHashMap<Long, DAGJob>();
    private DirectedAcyclicGraph<DAGJob, DefaultEdge> dag =
            new DirectedAcyclicGraph<DAGJob, DefaultEdge>(DefaultEdge.class);

    private static final Logger LOGGER = LogManager.getLogger("server");

    @Override
    public void init() {
        getSchedulerController().register(this);
        // load not deleted jobs from DB
        List<Job> jobs = jobService.getJobsNotDeleted();
        for (Job job : jobs) {
            if (job.getJobFlag() != JobFlag.DELETED.getValue()) {
                long jobId = job.getJobId();
                Set<Long> dependencies = jobDependService.getDependIds(jobId);
                int cycleFlag = job.getFixedDelay() > 0 ? 1 : 0;
                int dependFlag = cronService.getCronsByJobId(jobId).isEmpty() ? 0 : 1;
                int timeFlag = dependencies.isEmpty() ? 0 : 1;
                DAGJobType type = SchedulerUtil.getDAGJobType(cycleFlag, dependFlag, timeFlag);
                AddJobEvent addJobEvent = new AddJobEvent(jobId, dependencies, type);
                try {
                    handleAddJobEvent(addJobEvent);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }

    @Override
    public void destroy() {
        clear();
        getSchedulerController().unregister(this);
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
            DAGJobType type = event.getDAGJobType();
            addJob(jobId, new DAGJob(jobId, type), event.getDependencies());
        }
    }

    @VisibleForTesting
    protected void addJob(long jobId, DAGJob dagJob, Set<Long> dependencies) throws CycleFoundException {
        waitingTable.put(jobId, dagJob);
        dag.addVertex(dagJob);

        if (dependencies != null) {
            for (long d: dependencies) {
                DAGJob parent = waitingTable.get(d);
                if (parent != null) {
                    dag.addDagEdge(parent, dagJob);
                }
            }
        }
    }

    @VisibleForTesting
    protected void clear() {
        waitingTable.clear();

        Set<DAGJob> allJobs = dag.vertexSet();
        if (allJobs != null) {
            List<DAGJob> tmpJobs = new ArrayList<DAGJob>();
            tmpJobs.addAll(dag.vertexSet());
            dag.removeAllVertices(tmpJobs);
        }
    }

    /**
     * modify job flag
     *
     * @param long jobId
     */
    @Subscribe
    public void handleModifyJobFlagEvent(ModifyJobFlagEvent event) {
        long jobId = event.getJobId();
        JobFlag jobFlag = event.getJobFlag();
        DAGJob dagJob = waitingTable.get(jobId);
        List<DAGJob> children = new ArrayList<DAGJob>();
        if (dagJob != null) {
            children = getChildren(dagJob);
        }

        if (jobFlag.equals(JobFlag.DELETED)) {
            if (dagJob != null) {
                removeJob(dagJob);
            }
        } else {
            if (dagJob != null) {
                dagJob.setJobFlag(jobFlag);
            }
        }

        if (children != null) {
            // submit job if pass dependency check
            for (DAGJob child : children) {
               submitJobWithCheck(child);
            }
        }
    }

    @VisibleForTesting
    protected void removeJob(DAGJob dagJob) {
        if (dagJob != null) {
            waitingTable.remove(dagJob.getJobId());
            dag.removeVertex(dagJob);
        }
    }

    @Subscribe
    public void handleModifyJobEvent(ModifyJobEvent event) throws Exception {
        MODIFY_TYPE modifyType = event.getModifyType();
        long jobId = event.getJobId();
        Set<Long> newDependencies = event.getDependencies();
        // modify dependency
        if (modifyType.equals(MODIFY_TYPE.ADD)) {
            for (long d : newDependencies) {
                addDependency(d, jobId);
            }
        } else if (modifyType.equals(MODIFY_TYPE.DEL)) {
            for (long d : newDependencies) {
                removeDependency(d, jobId);
            }
        } else if (modifyType.equals(MODIFY_TYPE.MODIFY)) {
            DAGJob dagJob = waitingTable.get(jobId);
            if (dagJob != null) {
                modifyDependency(dagJob, newDependencies);
            }
        }

        // update dag job type
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            boolean hasDepend = (!newDependencies.isEmpty());
            boolean hasCron = event.isHasCron();
            boolean hasCycle = event.isHasCycle();
            dagJob.updateJobTypeByTimeFlag(hasCron);
            dagJob.updateJobTypeByDependFlag(hasDepend);
            dagJob.updateJobTypeByCycleFlag(hasCycle);
            submitJobWithCheck(dagJob);
        }
    }

    @VisibleForTesting
    public void addDependency(long parentId, long childId) throws CycleFoundException {
        DAGJob parent = waitingTable.get(parentId);
        DAGJob child = waitingTable.get(childId);
        if (parent != null && child != null) {
            dag.addDagEdge(parent, child);
        }
    }

    @VisibleForTesting
    protected void removeDependency(long parentId, long childId) {
        DAGJob parent = waitingTable.get(parentId);
        DAGJob child = waitingTable.get(childId);
        if (parent != null && child != null) {
            dag.removeEdge(parent, child);
        }
    }

    @VisibleForTesting
    protected void modifyDependency(DAGJob dagJob, Set<Long> newDependencies) throws CycleFoundException {
        Set<DefaultEdge> oldEdges = dag.incomingEdgesOf(dagJob);
        List<DefaultEdge> tmpEdges = new ArrayList<DefaultEdge>();
        tmpEdges.addAll(oldEdges);
        if (tmpEdges != null && !tmpEdges.isEmpty()) {
            dag.removeAllEdges(tmpEdges);
        }
        for (long d : newDependencies) {
            addDependency(d, dagJob.getJobId());
        }
    }

    /**
     * get dependent parent
     *
     * @param long jobId
     * @return parent job pair<jobid, jobFlag> list
     */
    public List<Pair<Long, JobFlag>> getParents(long jobId) {
        List<Pair<Long, JobFlag>> parentJobPairs = new ArrayList<Pair<Long, JobFlag>>();
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            List<DAGJob> parents = getParents(dagJob);
            if (parents != null) {
                for (DAGJob parent : parents) {
                    Pair<Long, JobFlag> jobPair = new Pair<Long, JobFlag>(
                            parent.getJobId(), parent.getJobFlag());
                    parentJobPairs.add(jobPair);
                }
            }
        }

        return parentJobPairs;
    }

    /**
     * get subsequent child
     *
     * @param long jobId
     * @return children job pair<jobid, jobFlag> list
     */
    public List<Pair<Long, JobFlag>> getChildren(long jobId) {
        List<Pair<Long, JobFlag>> childJobPairs = new ArrayList<Pair<Long, JobFlag>>();
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            List<DAGJob> children = getChildren(dagJob);
            if (children != null) {
                for (DAGJob child : children) {
                    Pair<Long, JobFlag> jobPair = new Pair<Long, JobFlag>(
                            child.getJobId(), child.getJobFlag());
                    childJobPairs.add(jobPair);
                }
            }
        }

        return childJobPairs;
    }

    @Subscribe
    public void handleTimeReadyEvent(TimeReadyEvent e) {
        long jobId = e.getJobId();
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            if (!(dagJob.getType().implies(DAGJobType.TIME))) {
                LOGGER.warn("Job {} doesn't imply TIME type , auto fix to add TIME type.", e.getJobId());
                dagJob.updateJobTypeByTimeFlag(true);
            }
            // 更新时间标识
            dagJob.setTimeReadyFlag();
            // 如果通过依赖检查，提交给taskScheduler，并重置自己的依赖状态
            submitJobWithCheck(dagJob);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleSuccessEvent(SuccessEvent e) {
        long jobId = e.getJobId();
        long taskId = e.getTaskId();
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            List<DAGJob> children = getChildren(dagJob);
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
    public void handleFailedEvent(FailedEvent e) {
        long jobId = e.getJobId();
        long taskId = e.getTaskId();
        DAGJob dagJob = waitingTable.get(jobId);
        if (dagJob != null) {
            List<DAGJob> children = getChildren(dagJob);
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
        List<DAGJob> parents = getParents(dagJob);
        Set<Long> needJobs = Sets.newHashSet();
        // get enabled parents
        if (parents != null) {
            for (DAGJob parent : parents) {
                if (parent.getJobFlag().equals(JobFlag.ENABLE)) {
                    needJobs.add(parent.getJobId());
                }
            }
        }
        if (dagJob.dependCheck(needJobs)) {
            taskScheduler.submitJob(dagJob.getJobId());
            dagJob.resetDependStatus();
        }
    }

    private List<DAGJob> getParents(DAGJob dagJob) {
        List<DAGJob> parents = new ArrayList<DAGJob>();
        Set<DefaultEdge> inEdges = dag.incomingEdgesOf(dagJob);
        if (inEdges != null) {
            for (DefaultEdge edge : inEdges) {
                parents.add(dag.getEdgeSource(edge));
            }
        }
        return parents;
    }

    private List<DAGJob> getChildren(DAGJob dagJob) {
        List<DAGJob> children = new ArrayList<DAGJob>();
        Set<DefaultEdge> outEdges = dag.outgoingEdgesOf(dagJob);
        if (outEdges != null) {
            for (DefaultEdge edge : outEdges) {
                children.add(dag.getEdgeTarget(edge));
            }
        }
        return children;
    }
}
