/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月2日 上午11:54:45
 */

package com.mogujie.jarvis.server.scheduler.event;

import java.util.Set;

import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;

/**
 * @author guangming
 *
 */
public class AddJobEvent extends DAGJobEvent{
    private Set<Long> dependencies;
    private DAGJobType type;

    /**
     * @param long jobId
     * @param JobDescriptor jobDesc
     */
    public AddJobEvent(long jobId, Set<Long> dependencies,
            DAGJobType type) {
        super(jobId);
        this.dependencies = dependencies;
        this.type = type;
    }

    public Set<Long> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<Long> dependencies) {
        this.dependencies = dependencies;
    }

    public DAGJobType getDAGJobType() {
        return type;
    }

    public void setDAGJobType(DAGJobType type) {
        this.type = type;
    }
}
