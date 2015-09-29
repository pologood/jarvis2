/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月9日 下午2:43:18
 */

package com.mogujie.jarvis.server.scheduler.event;



/**
 * @author guangming
 *
 */
public class ModifyJobEvent extends DAGJobEvent {
    public enum MODIFY_TYPE {
        ADD,
        DEL,
        MODIFY
    }

    private boolean hasCron;
    private boolean hasCycle;

    public ModifyJobEvent(long jobId, boolean hasCron, boolean hasCycle) {
       super(jobId);
       this.hasCron = hasCron;
       this.hasCycle = hasCycle;
    }

    public boolean isHasCron() {
        return hasCron;
    }

    public void setHasCron(boolean hasCron) {
        this.hasCron = hasCron;
    }

    public boolean isHasCycle() {
        return hasCycle;
    }

    public void setHasCycle(boolean hasCycle) {
        this.hasCycle = hasCycle;
    }
}
