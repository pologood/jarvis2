/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月16日 上午9:16:41
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
import org.joda.time.DateTime;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.domain.TaskType;
import com.mogujie.jarvis.core.exeception.JobScheduleException;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.domain.ModifyDependEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependChecker;
import com.mogujie.jarvis.server.scheduler.event.AddPlanEvent;
import com.mogujie.jarvis.server.scheduler.event.AddTaskEvent;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.PlanUtil;

/**
 * @author guangming
 *
 */
public enum JobGraph {
    INSTANCE;

    private Map<Long, DAGJob> jobMap = new ConcurrentHashMap<Long, DAGJob>();
    private DirectedAcyclicGraph<DAGJob, DefaultEdge> dag = new DirectedAcyclicGraph<DAGJob, DefaultEdge>(DefaultEdge.class);
    private JobSchedulerController controller = JobSchedulerController.getInstance();
    private JobService jobService = Injectors.getInjector().getInstance(JobService.class);
    private TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);

    private static final Logger LOGGER = LogManager.getLogger();

    public synchronized void clear() {
        Set<DAGJob> allJobs = dag.vertexSet();
        if (allJobs != null) {
            List<DAGJob> tmpJobs = new ArrayList<DAGJob>();
            tmpJobs.addAll(dag.vertexSet());
            dag.removeAllVertices(tmpJobs);
        }
        jobMap.clear();
    }

    protected Map<Long, DAGJob> getJobMap() {
        return jobMap;
    }

    public DAGJob getDAGJob(long jobId) {
        return jobMap.get(jobId);
    }

    /**
     * get dependent parent
     *
     * @param jobId
     * @return List of parents'pair with jobid and JobFlag
     */
    public List<Pair<Long, JobStatus>> getParents(long jobId) {
        List<Pair<Long, JobStatus>> parentJobPairs = new ArrayList<Pair<Long, JobStatus>>();
        DAGJob dagJob = jobMap.get(jobId);
        if (dagJob != null) {
            List<DAGJob> parents = getParents(dagJob);
            if (parents != null) {
                for (DAGJob parent : parents) {
                    Pair<Long, JobStatus> jobPair = new Pair<Long, JobStatus>(parent.getJobId(), parent.getJobStatus());
                    parentJobPairs.add(jobPair);
                }
            }
        }

        return parentJobPairs;
    }

    /**
     * get subsequent child
     *
     * @param jobId
     * @return List of children'pair with jobid and JobFlag
     */
    public List<Pair<Long, JobStatus>> getChildren(long jobId) {
        List<Pair<Long, JobStatus>> childJobPairs = new ArrayList<Pair<Long, JobStatus>>();
        DAGJob dagJob = jobMap.get(jobId);
        if (dagJob != null) {
            List<DAGJob> children = getChildren(dagJob);
            if (children != null) {
                for (DAGJob child : children) {
                    Pair<Long, JobStatus> jobPair = new Pair<Long, JobStatus>(child.getJobId(), child.getJobStatus());
                    childJobPairs.add(jobPair);
                }
            }
        }

        return childJobPairs;
    }

    /**
     * get active time based job ids
     *
     */
    public List<Long> getActiveTimeBasedJobs() {
        List<Long> jobs = new ArrayList<Long>();
        for (DAGJob dagJob : jobMap.values()) {
            if (dagJob.getType().implies(DAGJobType.TIME) && dagJob.getJobStatus().equals(JobStatus.ENABLE)
                    && jobService.isActive(dagJob.getJobId())) {
                jobs.add(dagJob.getJobId());
            }
        }
        return jobs;
    }

    /**
     * Add job
     *
     * @param jobId
     * @param dagJob
     * @param dependencies set of dependency jobId
     * @throws JobScheduleException
     */
    public synchronized void addJob(long jobId, DAGJob dagJob, Set<Long> dependencies) throws JobScheduleException {
        if (jobMap.get(jobId) == null) {
            dag.addVertex(dagJob);
            LOGGER.debug("add DAGJob {} to graph successfully.", dagJob.toString());
            if (dependencies != null) {
                for (long d : dependencies) {
                    DAGJob parent = jobMap.get(d);
                    if (parent != null) {
                        try {
                            dag.addDagEdge(parent, dagJob);
                            LOGGER.debug("add dependency successfully, parent is {}, child is {}", parent.getJobId(), dagJob.getJobId());
                        } catch (CycleFoundException e) {
                            LOGGER.error(e);
                            dag.removeVertex(dagJob);
                            LOGGER.warn("rollback {}", dagJob);
                            throw new JobScheduleException(e);
                        }
                    }
                }
            }
            jobMap.put(jobId, dagJob);
            LOGGER.info("add DAGJob {} and dependency {} to JobGraph successfully.", dagJob.toString());
            submitJobWithCheck(dagJob, DateTime.now().getMillis());
        }
    }

    /**
     * Remove job
     *
     * @param jobId
     * @throws JobScheduleException
     */
    public synchronized void removeJob(long jobId) throws JobScheduleException {
        if (jobMap.containsKey(jobId)) {
            DAGJob dagJob = jobMap.get(jobId);
            dag.removeVertex(dagJob);
            jobMap.remove(jobId);
            LOGGER.info("remove DAGJob {} from DAGScheduler successfully.", jobId);
        }
    }

    public synchronized void removeJob(DAGJob dagJob) {
        if (dagJob != null) {
            jobMap.remove(dagJob.getJobId());
            dag.removeVertex(dagJob);
        }
    }

    /**
     * modify DAG job dependency
     *
     * @param jobId
     * @param dependEntries List of ModifyDependEntry
     */
    public void modifyDependency(long jobId, List<ModifyDependEntry> dependEntries) throws CycleFoundException {
        for (ModifyDependEntry entry : dependEntries) {
            long preJobId = entry.getPreJobId();
            if (entry.getOperation().equals(OperationMode.ADD)) {
                addDependency(preJobId, jobId);
                LOGGER.info("add dependency successfully, parent {}, child {}", preJobId, jobId);
            } else if (entry.getOperation().equals(OperationMode.DELETE)) {
                removeDependency(preJobId, jobId);
                LOGGER.info("remove dependency successfully, parent {}, child {}", preJobId, jobId);
            } else if (entry.getOperation().equals(OperationMode.EDIT)) {
                modifyDependency(preJobId, jobId, entry.getOffsetStrategy());
                LOGGER.info("modify dependency strategy, new common strategy is {}, new offset Strategy is {}", entry.getCommonStrategy(),
                        entry.getOffsetStrategy());
            }
        }

        // update dag job type
        DAGJob dagJob = getDAGJob(jobId);
        if (dagJob != null) {
            DAGJobType oldType = dagJob.getType();
            boolean hasDepend = (!getParents(dagJob).isEmpty());
            dagJob.updateJobTypeByDependFlag(hasDepend);
            if (!oldType.equals(dagJob.getType())) {
                LOGGER.info("moidfy DAGJob type from {} to {}", oldType, dagJob.getType());
            }
            submitJobWithCheck(dagJob, DateTime.now().getMillis());
        }
    }

    /**
     * modify job flag
     *
     * @param jobId
     * @param jobStatus
     * @throws JobScheduleException
     */
    public void modifyJobFlag(long jobId, JobStatus oldStatus, JobStatus newStatus) throws JobScheduleException {
        DAGJob dagJob = getDAGJob(jobId);
        List<DAGJob> children = new ArrayList<DAGJob>();
        if (dagJob != null) {
            children = getChildren(dagJob);
        }

        if (newStatus.equals(JobStatus.DELETED)) {
            if (dagJob != null) {
                removeJob(dagJob);
                LOGGER.info("remove DAGJob {} from DAGScheduler successfully.", dagJob.getJobId());
            }
        }

        if (children != null) {
            // submit job if pass dependency check
            for (DAGJob child : children) {
                submitJobWithCheck(child, DateTime.now().getMillis());
            }
        }
    }

    /**
     * modify DAG job type
     *
     * @param jobId
     * @param newType
     * @throws JobScheduleException
     */
    public void modifyDAGJobType(long jobId, DAGJobType newType) {
        // update dag job type
        DAGJob dagJob = getDAGJob(jobId);
        if (dagJob != null) {
            dagJob.setType(newType);
        }
    }

    public synchronized List<DAGJob> getParents(DAGJob dagJob) {
        List<DAGJob> parents = new ArrayList<DAGJob>();
        Set<DefaultEdge> inEdges = dag.incomingEdgesOf(dagJob);
        if (inEdges != null) {
            for (DefaultEdge edge : inEdges) {
                parents.add(dag.getEdgeSource(edge));
            }
        }
        return parents;
    }

    public synchronized List<DAGJob> getChildren(DAGJob dagJob) {
        List<DAGJob> children = new ArrayList<DAGJob>();
        Set<DefaultEdge> outEdges = dag.outgoingEdgesOf(dagJob);
        if (outEdges != null) {
            for (DefaultEdge edge : outEdges) {
                children.add(dag.getEdgeTarget(edge));
            }
        }
        return children;
    }

    public synchronized List<DAGJob> getActiveChildren(DAGJob dagJob) {
        List<DAGJob> children = new ArrayList<DAGJob>();
        Set<DefaultEdge> outEdges = dag.outgoingEdgesOf(dagJob);
        if (outEdges != null) {
            for (DefaultEdge edge : outEdges) {
                DAGJob child = dag.getEdgeTarget(edge);
                if (child.getJobStatus().equals(JobStatus.ENABLE) && jobService.isActive(child.getJobId())) {
                    children.add(dag.getEdgeTarget(edge));
                }
            }
        }
        return children;
    }

    /**
     * submit job if pass the dependency check
     * 如果不是单亲纯依赖，必须配置调度时间
     *
     * @param dagJob
     * @param scheduleTime
     */
    public void submitJobWithCheck(DAGJob dagJob, long scheduleTime) {
        long jobId = dagJob.getJobId();
        // 如果是时间任务，遍历自己的调度时间做依赖检查
        if (dagJob.getType().implies(DAGJobType.TIME)) {
            List<Long> timeStamps = getUnScheduledTimeStamps(jobId);
            for (long timeStamp : timeStamps) {
                if (dagJob.checkDependency(timeStamp)) {
                    LOGGER.info("{} pass the dependency check", dagJob);

                    // 提交给TimeScheduler进行时间调度
                    Map<Long, List<Long>> dependTaskIdMap = dagJob.getDependTaskIdMap(timeStamp);
                    AddPlanEvent event = new AddPlanEvent(jobId, scheduleTime, dependTaskIdMap);
                    controller.notify(event);
                }
            }
        } else {
            Set<Long> needJobs = getParentJobIds(dagJob.getJobId());
            // 如果是单亲纯依赖，表示runtime，不需要做依赖检查了，直接提交给TaskScheduler
            if (needJobs.size() == 1) {
                long preJobId = needJobs.iterator().next();
                Task task = taskService.getTaskByJobIdAndScheduleTime(preJobId, scheduleTime);
                if (task != null) {
                    long taskId = task.getTaskId();
                    Map<Long, List<Long>> dependTaskIdMap = Maps.newHashMap();
                    dependTaskIdMap.put(preJobId, Lists.newArrayList(taskId));
                    AddTaskEvent event = new AddTaskEvent(jobId, dependTaskIdMap, scheduleTime);
                    controller.notify(event);
                }
            } else {
                LOGGER.warn("不是单亲纯依赖必须配置调度时间！！");
            }
        }
    }

    @VisibleForTesting
    public synchronized void addDependency(long parentId, long childId) throws CycleFoundException {
        DAGJob parent = jobMap.get(parentId);
        DAGJob child = jobMap.get(childId);
        if (parent != null && child != null) {
            dag.addDagEdge(parent, child);
        }
    }

    @VisibleForTesting
    protected synchronized void removeDependency(long parentId, long childId) {
        DAGJob parent = jobMap.get(parentId);
        DAGJob child = jobMap.get(childId);
        if (parent != null && child != null) {
            dag.removeEdge(parent, child);
        }
    }

    protected void modifyDependency(long parentId, long childId, String offsetStrategy) {
        DAGJob parent = jobMap.get(parentId);
        DAGJob child = jobMap.get(childId);
        if (parent != null && child != null) {
            DAGDependChecker checker = child.getDependChecker();
            checker.updateExpression(parentId, offsetStrategy);
        }
    }

    public Set<Long> getParentJobIds(long jobId) {
        DAGJob dagJob = jobMap.get(jobId);
        Set<Long> jobIds = Sets.newHashSet();
        if (dagJob != null) {
            jobIds = getParentJobIds(dagJob);
        }
        return jobIds;
    }

    private Set<Long> getParentJobIds(DAGJob dagJob) {
        List<DAGJob> parents = getParents(dagJob);
        Set<Long> jobIds = Sets.newHashSet();
        if (parents != null) {
            for (DAGJob parent : parents) {
                jobIds.add(parent.getJobId());
            }
        }
        return jobIds;
    }

    /**
     * 从上一次调度找到当前时间的下一次，应该要调度但是尚未开始调度的时间
     * 为什么要找到当前时间下一次？比如C依赖A和B，A是1点，B是2点，当前时间3点，C时间是6点，那必须找到下一次才保险
     *
     * @param jobId
     * @return
     */
    private List<Long> getUnScheduledTimeStamps(long jobId) {
        List<Long> timeStamps = new ArrayList<Long>();
        Task lastTask = taskService.getLastTask(jobId, DateTime.now().getMillis(), TaskType.SCHEDULE);
        if (lastTask != null) {
            DateTime startTime = PlanUtil.getScheduleTimeAfter(jobId, new DateTime(lastTask.getDataTime()));
            DateTime endTime = PlanUtil.getScheduleTimeAfter(jobId, DateTime.now());
            timeStamps.add(startTime.getMillis());
            DateTime nextTime = startTime;
            while (nextTime.isBefore(endTime)) {
                nextTime = PlanUtil.getScheduleTimeAfter(jobId, nextTime);
                timeStamps.add(nextTime.getMillis());
            }
        }
        return timeStamps;
    }

}
