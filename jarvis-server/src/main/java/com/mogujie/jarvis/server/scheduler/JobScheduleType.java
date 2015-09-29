/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午8:39:44
 */

package com.mogujie.jarvis.server.scheduler;

import com.mogujie.jarvis.server.scheduler.dag.DAGJob;

/**
 * @author guangming
 *
 */
public enum JobScheduleType {
    //time based schedule job
    CRONTAB(DAGJob.class.getName()),

    //dependency based schedule job
    DEPENDENCY(DAGJob.class.getName()),

    //time based + dependency based schedule job
    CRON_DEPEND(DAGJob.class.getName()),

    //fixed deploy job
    CYCLE(DAGJob.class.getName()),

    OTHER("");

    private String value;

    JobScheduleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
