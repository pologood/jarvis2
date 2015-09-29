/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月29日 下午4:00:01
 */

package com.mogujie.jarvis.server.scheduler.event;

import java.util.List;

import com.mogujie.jarvis.server.domain.ModifyDependEntry;

/**
 * @author guangming
 *
 */
public class ModifyDependencyEvent extends DAGJobEvent {

    public ModifyDependencyEvent(long jobId, List<ModifyDependEntry> dependEntries) {
        super(jobId);
        this.dependEntries = dependEntries;
    }

    private List<ModifyDependEntry> dependEntries;

    public List<ModifyDependEntry> getDependEntries() {
        return dependEntries;
    }

    public void setDependEntries(List<ModifyDependEntry> dependEntries) {
        this.dependEntries = dependEntries;
    }

    public void addDependEntry(ModifyDependEntry entry) {
        dependEntries.add(entry);
    }
}
