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
import java.util.Map.Entry;
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
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.exeception.JobScheduleException;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.domain.ModifyDependEntry;
import com.mogujie.jarvis.server.domain.ModifyOperation;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependChecker;
import com.mogujie.jarvis.server.scheduler.event.AddTaskEvent;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;

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
            dagJob.clearTimeStamp();
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
            if (entry.getOperation().equals(ModifyOperation.ADD)) {
                addDependency(preJobId, jobId);
                LOGGER.info("add dependency successfully, parent {}, child {}", preJobId, jobId);
            } else if (entry.getOperation().equals(ModifyOperation.DEL)) {
                removeDependency(preJobId, jobId);
                LOGGER.info("remove dependency successfully, parent {}, child {}", preJobId, jobId);
            } else if (entry.getOperation().equals(ModifyOperation.MODIFY)) {
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
    public void modifyJobFlag(long jobId, JobStatus jobStatus) throws JobScheduleException {
        DAGJob dagJob = getDAGJob(jobId);
        List<DAGJob> children = new ArrayList<DAGJob>();
        if (dagJob != null) {
            children = getChildren(dagJob);
        }

        if (jobStatus.equals(JobStatus.DELETED)) {
            if (dagJob != null) {
                removeJob(dagJob);
                LOGGER.info("remove DAGJob {} from DAGScheduler successfully.", dagJob.getJobId());
            }
        } else {
            if (dagJob != null) {
                JobStatus oldFlag = dagJob.getJobStatus();
                dagJob.setJobStatus(jobStatus);
                LOGGER.info("moidfy job flag from {} to {}.", oldFlag, jobStatus);
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
            submitJobWithCheck(dagJob, DateTime.now().getMillis());
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
     * 如果该任务是串行任务，或者依赖关系中至少有一个是对过去的偏移依赖，则必须配置调度时间
     *
     * @param dagJob
     * @param scheduleTime
     */
    public void submitJobWithCheck(DAGJob dagJob, long scheduleTime) {
        // 如果是时间任务，遍历自己的调度时间做依赖检查
        if (dagJob.getType().implies(DAGJobType.TIME)) {
            List<Long> timeStamps = dagJob.getTimeStamps();

            long jobId = dagJob.getJobId();
            // 如果是串行任务
            if (jobService.get(jobId).getJob().getIsSerial()) {
                if (timeStamps.size() > 0) {
                    // 只触发第一个
                    long timeStamp = timeStamps.get(0);
                    // 首先检查自己上一次是否成功
                    Task task = taskService.getLastTask(jobId, timeStamp);
                    if (task == null || task.getStatus().equals(TaskStatus.SUCCESS.getValue())) {
                        // 然后进行依赖检查
                        if (dagJob.checkDependency(timeStamp)) {
                            LOGGER.info("{} pass the dependency check", dagJob);

                            // submit task to task scheduler
                            Map<Long, List<Task>> dependTaskMap = dagJob.getDependTaskMap(timeStamp);
                            Map<Long, List<Long>> dependTaskIdMap = convert2DependTaskIdMap(dependTaskMap);
                            // 串行任务，添加自依赖
                            List<Long> dependTaskIds = Lists.newArrayList(task.getTaskId());
                            dependTaskIdMap.put(jobId, dependTaskIds);

                            AddTaskEvent event = new AddTaskEvent(jobId, dependTaskIdMap, timeStamp);
                            controller.notify(event);

                            // remove time stamp
                            dagJob.removeTimeStamp(timeStamp);
                        }
                    } else {
                        LOGGER.info("Serial Task, but last {} not successed", task.getTaskId());
                    }
                }
            } else {
                for (long timeStamp : timeStamps) {
                    if (dagJob.checkDependency(timeStamp)) {
                        LOGGER.info("{} pass the dependency check", dagJob);

                        // submit task to task scheduler
                        Map<Long, List<Task>> dependTaskMap = dagJob.getDependTaskMap(timeStamp);
                        Map<Long, List<Long>> dependTaskIdMap = convert2DependTaskIdMap(dependTaskMap);
                        AddTaskEvent event = new AddTaskEvent(jobId, dependTaskIdMap, timeStamp);
                        controller.notify(event);

                        // remove time stamp
                        dagJob.removeTimeStamp(timeStamp);
                    }
                }
            }
        } else {
            Set<Long> needJobs = getEnableParentJobIds(dagJob.getJobId());
            // 如果是单亲纯依赖，表示runtime，不需要做依赖检查了
            if (needJobs.size() == 1) {
                long jobId = needJobs.iterator().next();
                Task task = taskService.getTaskByJobIdAndScheduleTime(jobId, scheduleTime);
                if (task != null) {
                    long taskId = task.getTaskId();
                    Map<Long, List<Long>> dependTaskIdMap = Maps.newHashMap();
                    dependTaskIdMap.put(jobId, Lists.newArrayList(taskId));
                    AddTaskEvent event = new AddTaskEvent(jobId, dependTaskIdMap, scheduleTime);
                    controller.notify(event);
                }
            } else if (needJobs.size() > 1) {
                // 如果是多亲纯依赖，根据传进来的调度时间进行依赖检查
                // 当前这种情况可以不支持，如果是多亲依赖必须配置调度时间
                if (dagJob.checkDependency(scheduleTime)) {
                    long jobId = dagJob.getJobId();
                    LOGGER.info("{} pass the dependency check", dagJob);

                    // submit task to task scheduler
                    Map<Long, List<Task>> dependTaskMap = dagJob.getDependTaskMap(scheduleTime);
                    Map<Long, List<Long>> dependTaskIdMap = convert2DependTaskIdMap(dependTaskMap);
                    long lastScheduleTime = getLastScheduleTime(dependTaskMap);
                    AddTaskEvent event = new AddTaskEvent(jobId, dependTaskIdMap, lastScheduleTime);
                    controller.notify(event);
                }
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

    public Set<Long> getEnableParentJobIds(long jobId) {
        DAGJob dagJob = jobMap.get(jobId);
        Set<Long> jobIds = Sets.newHashSet();
        if (dagJob != null) {
            jobIds = getEnableParentJobIds(dagJob);
        }
        return jobIds;
    }

    private Set<Long> getEnableParentJobIds(DAGJob dagJob) {
        List<DAGJob> parents = getParents(dagJob);
        Set<Long> jobIds = Sets.newHashSet();
        if (parents != null) {
            for (DAGJob parent : parents) {
                if (parent.getJobStatus().equals(JobStatus.ENABLE)) {
                    jobIds.add(parent.getJobId());
                }
            }
        }
        return jobIds;
    }

    private long getLastScheduleTime(Map<Long, List<Task>> dependTaskMap) {
        long scheduleTime = 0;
        for (List<Task> tasks : dependTaskMap.values()) {
            for (Task task : tasks) {
                if (task.getScheduleTime().getTime() > scheduleTime) {
                    scheduleTime = task.getScheduleTime().getTime();
                }
            }
        }
        return scheduleTime;
    }

    private Map<Long, List<Long>> convert2DependTaskIdMap(Map<Long, List<Task>> dependTaskMap) {
        Map<Long, List<Long>> dependTaskIdMap = Maps.newHashMap();
        for (Entry<Long, List<Task>> entry : dependTaskMap.entrySet()) {
            long preJobId = entry.getKey();
            List<Task> dependTasks = entry.getValue();
            List<Long> dependTaskIds = new ArrayList<Long>();
            for (Task task : dependTasks) {
                dependTaskIds.add(task.getTaskId());
            }
            dependTaskIdMap.put(preJobId, dependTaskIds);
        }
        return dependTaskIdMap;
    }
}
