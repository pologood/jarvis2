/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午2:00:30
 */

package com.mogujie.jarvis.server.scheduler.dag.event;


/**
 * @author guangming
 *
 */
public abstract class DAGTaskEvent extends DAGJobEvent {
    private long taskid;

    public DAGTaskEvent(long jobid, long taskid) {
        super(jobid);
        this.taskid = taskid;
    }

    public long getTaskid() {
        return taskid;
    }

    public void setTaskid(long taskid) {
        this.taskid = taskid;
    }
}
