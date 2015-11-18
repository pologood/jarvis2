/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:49:39
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.List;

import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;


/**
 * @author guangming
 *
 */
public abstract class AbstractTaskSchedule {

    private long myJobId;
    private long preJobId;
    private CommonStrategy commonStrategy;

    public AbstractTaskSchedule() {}

    public AbstractTaskSchedule(long myJobId, long preJobId, CommonStrategy commonStrategy) {
        this.myJobId = myJobId;
        this.preJobId = preJobId;
        this.commonStrategy = commonStrategy;
    }

    public long getMyJobId() {
        return myJobId;
    }

    public void setMyjobId(long jobId) {
        this.myJobId = jobId;
    }

    public long getPreJobId() {
        return preJobId;
    }

    public void setPreJobId(long preJobId) {
        this.preJobId = preJobId;
    }

    public CommonStrategy getCommonStrategy() {
        return commonStrategy;
    }

    public void setCommonStrategy(CommonStrategy commonStrategy) {
        this.commonStrategy = commonStrategy;
    }

    /**
     * init
     */
    public void init() {}

    /**
     * reset selected
     */
    public void resetSchedule() {}

    /**
     * finish schedule
     */
    public void finishSchedule() {}

    /**
     * check dependency
     */
    public abstract boolean check(long scheduleTime);

    /**
     * start schedule task
     */
    public void scheduleTask(long taskId, long scheduleTime) {}

    /**
     * get scheduling tasks
     */
    public abstract List<ScheduleTask> getSchedulingTasks();

    /**
     * get selected tasks
     */
    public abstract List<ScheduleTask> getSelectedTasks();

}
