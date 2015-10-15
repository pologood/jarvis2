/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月15日 下午3:30:04
 */

package com.mogujie.jarvis.server.scheduler.task;

import com.mogujie.jarvis.dto.Task;

/**
 * @author guangming
 *
 */
public class FailedTask {
    private Task task;
    private long nextStartTime;

    public FailedTask(Task task, long time) {
        this.task = task;
        this.nextStartTime = time;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public long getNextStartTime() {
        return nextStartTime;
    }

    public void setNextStartTime(long nextStartTime) {
        this.nextStartTime = nextStartTime;
    }
}
