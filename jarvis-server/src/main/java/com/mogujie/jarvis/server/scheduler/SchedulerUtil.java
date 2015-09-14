/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:42:32
 */

package com.mogujie.jarvis.server.scheduler;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.common.util.ReflectionUtils;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.server.scheduler.dag.status.AbstractDependStatus;
import com.mogujie.jarvis.server.scheduler.dag.status.MysqlCachedDependStatus;

/**
 * @author guangming
 *
 */
public class SchedulerUtil {
    public static String JOB_DEPEND_STATUS_KEY = "job.depend.status";
    public static String DEFAULT_JOB_DEPEND_STATUS = MysqlCachedDependStatus.class.getName();

    public static AbstractDependStatus getJobDependStatus(Configuration conf) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        String className = conf.getString(JOB_DEPEND_STATUS_KEY, DEFAULT_JOB_DEPEND_STATUS);
        return ReflectionUtils.getInstanceByClassName(className);
    }

    public static JobScheduleType getJobScheduleType(boolean hasCron, boolean hasDepend) {
        JobScheduleType type;
        if (hasCron) {
            if (hasDepend) {
                type = JobScheduleType.CRON_DEPEND;
            } else {
                type = JobScheduleType.CRONTAB;
            }
        } else {
            if (hasDepend) {
                type = JobScheduleType.DEPENDENCY;
            } else {
                type = JobScheduleType.OTHER;
            }
        }
        return type;
    }

    public static Job convert2Job(RestServerSubmitJobRequest msg) {
        Job job = new Job();
        // TODO
        return job;
    }

    public static Job convert2Job(RestServerModifyJobRequest msg) {
        Job job = new Job();
        // TODO
        return job;
    }

}
