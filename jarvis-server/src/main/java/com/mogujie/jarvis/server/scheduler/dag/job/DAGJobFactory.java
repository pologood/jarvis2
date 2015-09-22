/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午9:02:36
 */

package com.mogujie.jarvis.server.scheduler.dag.job;

import com.mogujie.jarvis.core.common.util.ReflectionUtils;
import com.mogujie.jarvis.server.scheduler.JobScheduleType;

/**
 * @author guangming
 *
 */
public class DAGJobFactory {
    public static DAGJob createDAGJob(JobScheduleType jobType, long jobId) throws ClassNotFoundException {
        String className = jobType.getValue();
        DAGJob dagJob = ReflectionUtils.getInstanceByClassName(className);
        if (dagJob != null) {
            dagJob.setJobId(jobId);
            if (jobType.equals(JobScheduleType.CRONTAB) ||
                    jobType.equals(JobScheduleType.CRON_DEPEND)) {
                dagJob.setHasTimeFlag(true);
            }
        }

        return dagJob;
    }
}
