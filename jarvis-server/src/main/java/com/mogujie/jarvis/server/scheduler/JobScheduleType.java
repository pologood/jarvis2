/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午8:39:44
 */

package com.mogujie.jarvis.server.scheduler;

import com.mogujie.jarvis.server.scheduler.dag.job.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.job.DAGOffsetJob;
import com.mogujie.jarvis.server.scheduler.dag.job.TimeDAGJob;

/**
 * @author guangming
 *
 */
public enum JobScheduleType {
    //time based schedule job
    CRONTAB(""),

    //dependency based schedule job
    DEPENDENCY(DAGJob.class.getName()),

    //time based + dependency based schedule job
    CRON_DEPEND(TimeDAGJob.class.getName()),

    //dependency based + offset
    DEPEND_OFFSET(DAGOffsetJob.class.getName()),

    //cycle based schedule job
    CYCLE(""),

    OTHER("");

    private String value;

    JobScheduleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
