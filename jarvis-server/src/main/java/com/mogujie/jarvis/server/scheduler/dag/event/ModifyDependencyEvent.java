/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月9日 下午2:43:18
 */

package com.mogujie.jarvis.server.scheduler.dag.event;

import java.util.Set;


/**
 * @author guangming
 *
 */
public class ModifyDependencyEvent extends DAGJobEvent {
    private Set<Long> dependencies;
    private MODIFY_TYPE modifyType;
    public enum MODIFY_TYPE {
        ADD,
        DEL,
        MODIFY
    }

    public ModifyDependencyEvent(long jobId, Set<Long> dependencies, MODIFY_TYPE type) {
       super(jobId);
       this.dependencies = dependencies;
       this.modifyType = type;
    }

    public Set<Long> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<Long> dependencies) {
        this.dependencies = dependencies;
    }

    public MODIFY_TYPE getModifyType() {
        return modifyType;
    }

    public void setModifyType(MODIFY_TYPE modifyType) {
        this.modifyType = modifyType;
    }
}
