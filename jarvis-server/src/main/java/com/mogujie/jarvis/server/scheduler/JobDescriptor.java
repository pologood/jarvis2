/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:26:03
 */

package com.mogujie.jarvis.server.scheduler;

import java.util.Set;

import com.mogujie.jarvis.core.JobContext;
import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;

/**
 * @author guangming
 *
 */
public class JobDescriptor {
    private JobContext jobContext;
    private JobScheduleType scheduleType;
    private JobDependencyStrategy jobDepenStrategy;

    JobDescriptor(JobContext jobContext, Set<Integer> needDependencies,
            JobScheduleType scheduleType, JobDependencyStrategy jobDepenStrategy) {
        this.jobContext = jobContext;
        this.scheduleType = scheduleType;
        this.jobDepenStrategy = jobDepenStrategy;
    }

    public JobContext getJobContext() {
        return jobContext;
    }

    public void setJobContext(JobContext jobContext) {
        this.jobContext = jobContext;
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
