/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:26:03
 */

package com.mogujie.jarvis.server.scheduler;

import java.util.Set;

import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;

/**
 * @author guangming
 *
 */
public class JobDescriptor {
    private Job job;
    private Set<Long> needDependencies;
    private JobScheduleType scheduleType;
    private JobDependencyStrategy jobDepenStrategy;

    public JobDescriptor(Job job, Set<Long> needDependencies,
            JobScheduleType scheduleType, JobDependencyStrategy jobDepenStrategy) {
        this.job = job;
        this.needDependencies = needDependencies;
        this.scheduleType = scheduleType;
        this.jobDepenStrategy = jobDepenStrategy;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Set<Long> getNeedDependencies() {
        return needDependencies;
    }

    public void setNeedDependencies(Set<Long> needDependencies) {
        this.needDependencies = needDependencies;
    }

    public JobScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(JobScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public JobDependencyStrategy getJobDepenStrategy() {
        return jobDepenStrategy;
    }

    public void setJobDepenStrategy(JobDependencyStrategy jobDepenStrategy) {
        this.jobDepenStrategy = jobDepenStrategy;
    }
}
