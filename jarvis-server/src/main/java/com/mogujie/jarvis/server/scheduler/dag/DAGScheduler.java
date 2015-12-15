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
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.event.ScheduleEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;

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
            dagJob.setTimeReadyFlag();
            LOGGER.info("DAGJob {} time ready", dagJob.getJobId());
            // 如果通过依赖检查，提交给taskScheduler，并重置自己的依赖状态
            jobGraph.submitJobWithCheck(dagJob, scheduleTime);
        }
    }

    @Subscribe
    public void handleScheduleEvent(ScheduleEvent e) {
        long jobId = e.getJobId();
        long taskId = e.getTaskId();
        long scheduleTime = e.getScheduleTime();
        long childJobId = e.getChildJobId();
        LOGGER.info("start handleScheduleEvent, jobId={}, scheduleTime={}, taskId={}, "
                + "childJobId={}", jobId, scheduleTime, taskId, childJobId);
        DAGJob dagJob = getDAGJob(jobId);
        if (dagJob != null) {
            if (childJobId == 0) {
                List<DAGJob> children = jobGraph.getActiveChildren(dagJob);
                if (children != null) {
                    for (DAGJob child : children) {
                        if (child.getJobFlag().equals(JobFlag.ENABLE)) {
                            child.scheduleTask(jobId, taskId, scheduleTime);
                            jobGraph.submitJobWithCheck(child);
                        }
                    }
                }
            } else {
                DAGJob child = getDAGJob(childJobId);
                if (child != null) {
                    if (child.getJobFlag().equals(JobFlag.ENABLE)) {
                        child.scheduleTask(jobId, taskId, scheduleTime);
                        jobGraph.submitJobWithCheck(child);
                    }
                }
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