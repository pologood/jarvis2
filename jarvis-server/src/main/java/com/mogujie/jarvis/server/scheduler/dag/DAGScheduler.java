/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:07
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.event.ScheduleEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;
import com.mogujie.jarvis.server.service.JobService;

/**
 * Scheduler used to handle dependency based job.
 *
 * @author guangming
 *
 */
public class DAGScheduler extends Scheduler {
    private static DAGScheduler instance = new DAGScheduler();
    private DAGScheduler() {}
    public static DAGScheduler getInstance() {
        return instance;
    }

    private JobGraph jobGraph = JobGraph.INSTANCE;
    private JobService jobService = Injectors.getInjector().getInstance(JobService.class);
    private static final Logger LOGGER = LogManager.getLogger();

    public void destroy() {
        jobGraph.clear();
    }

    @Override
    public void handleStartEvent(StartEvent event) {
    }

    @Override
    public void handleStopEvent(StopEvent event) {
    }

    /**
     * 由TimeScheduler发送TimeReadyEvent，DAGScheduler进行处理。
     * 首先更新该DAGJob的时间标识，然后进行依赖检查
     *
     * @param e
     */
    @Subscribe
    public void handleTimeReadyEvent(TimeReadyEvent e) {
        long jobId = e.getJobId();
        long scheduleTime = e.getScheduleTime();
        LOGGER.info("start handleTimeReadyEvent, jobId={}, scheduleTime={}", jobId, scheduleTime);
        DAGJob dagJob = getDAGJob(jobId);
        if (dagJob != null) {
            if (!(dagJob.getType().implies(DAGJobType.TIME))) {
                LOGGER.error("{} doesn't imply TIME type, but receive {} ", dagJob, e);
                return;
            }
            // 更新时间标识
            dagJob.addTimeStamp(scheduleTime);
            LOGGER.info("DAGJob {} time ready", dagJob.getJobId());
            // 如果通过依赖检查，提交给taskScheduler，并移除自己的时间戳
            jobGraph.submitJobWithCheck(dagJob, scheduleTime);
        }
    }

    /**
     * 由TaskScheduler发送ScheduleEvent，DAGScheduler进行处理。
     *
     * @param e
     */
    @Subscribe
    public void handleScheduleEvent(ScheduleEvent e) {
        long jobId = e.getJobId();
        long taskId = e.getTaskId();
        long scheduleTime = e.getScheduleTime();
        LOGGER.info("start handleScheduleEvent, jobId={}, scheduleTime={}, taskId={}",
                jobId, scheduleTime, taskId);
        DAGJob dagJob = getDAGJob(jobId);
        if (dagJob != null) {
            List<DAGJob> children = jobGraph.getActiveChildren(dagJob);
            // 如果有子任务，触发子任务
            if (children != null && !children.isEmpty()) {
                for (DAGJob child : children) {
                    if (child.getJobStatus().equals(JobStatus.ENABLE)) {
                        jobGraph.submitJobWithCheck(child, scheduleTime);
                    }
                }
            } else if (jobService.get(jobId).getJob().getSerialFlag() > 0) {
                // 如果是串行任务，触发自己
                jobGraph.submitJobWithCheck(dagJob, scheduleTime);
            }
        }
    }

    public JobGraph getJobGraph() {
        return jobGraph;
    }

    private DAGJob getDAGJob(long jobId) {
        return jobGraph.getDAGJob(jobId);
    }

}