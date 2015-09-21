/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月2日 上午11:54:45
 */

package com.mogujie.jarvis.server.scheduler.event;

import java.util.Set;

import com.mogujie.jarvis.server.scheduler.JobScheduleType;

/**
 * @author guangming
 *
 */
public class AddJobEvent extends DAGJobEvent{
    private Set<Long> dependencies;
    private JobScheduleType scheduleType;

    /**
     * @param long jobId
     * @param JobDescriptor jobDesc
     */
    public AddJobEvent(long jobId, Set<Long> dependencies,
            JobScheduleType type) {
        super(jobId);
        this.dependencies = dependencies;
        this.scheduleType = type;
    }

    public Set<Long> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<Long> dependencies) {
        this.dependencies = dependencies;
    }

    public JobScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(JobScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }
}
