/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午2:00:30
 */

package com.mogujie.jarvis.server.scheduler.event;

import com.mogujie.jarvis.server.domain.JobKey;


/**
 * @author guangming
 *
 */
public abstract class DAGTaskEvent extends DAGJobEvent {
    private long taskId;

    public DAGTaskEvent(JobKey jobKey, long taskId) {
        super(jobKey);
        this.taskId = taskId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }
}
