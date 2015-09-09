/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月9日 下午2:43:18
 */

package com.mogujie.jarvis.server.scheduler.dag.event;

import com.mogujie.jarvis.server.observer.Event;

/**
 * @author guangming
 *
 */
public class ModifyDependencyEvent implements Event {
    long parentId;
    long childId;
    boolean isAddDepend;

    public ModifyDependencyEvent(long parentId, long childId, boolean isAddDepend) {
        this.parentId = parentId;
        this.childId = childId;
        this.isAddDepend = isAddDepend;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    public boolean isAddDepend() {
        return isAddDepend;
    }

    public void setAddDepend(boolean isAddDepend) {
        this.isAddDepend = isAddDepend;
    }
}
