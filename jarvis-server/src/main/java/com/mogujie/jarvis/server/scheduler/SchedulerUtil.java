/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:42:32
 */

package com.mogujie.jarvis.server.scheduler;

import java.text.DateFormat;
import java.util.Date;

import com.mogujie.jarvis.core.common.util.JsonHelper;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;

/**
 * @author guangming
 *
 */
public class SchedulerUtil {
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
        job.setAppName(msg.getAppName());
        job.setJobName(msg.getJobName());
        job.setContent(msg.getContent());
        job.setPriority(msg.getPriority());
        job.setJobFlag(JobFlag.ENABLE.getValue());
        job.setJobType(msg.getJobType());
        job.setActiveStartDate(new Date(msg.getStartTime()));
        job.setActiveEndDate(new Date(msg.getEndTime()));
        job.setFailedAttempts(msg.getFailedRetries());
        job.setFailedInterval(msg.getFailedInterval());
        job.setRejectAttempts(msg.getRejectRetries());
        job.setRejectInterval(msg.getRejectInterval());
        job.setSubmitUser(msg.getUser());
        job.setUpdateUser(msg.getUser());
        job.setWorkerGroupId(msg.getGroupId());
        Date currentTime = new Date();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
        dateTimeFormat.format(currentTime);
        job.setCreateTime(currentTime);
        job.setUpdateTime(currentTime);
        job.setParams(JsonHelper.parseMapEntryList2JSON(msg.getParametersList()));
        job.setOriginJobId(msg.getOriginJobId());
        return job;
    }

    public static Job convert2Job(RestServerModifyJobRequest msg) {
        Job job = new Job();
        job.setJobId(msg.getJobId());
        job.setContent(msg.getCommand());
        job.setPriority(msg.getPriority());
        job.setActiveStartDate(new Date(msg.getStartTime()));
        job.setActiveEndDate(new Date(msg.getEndTime()));
        job.setFailedAttempts(msg.getFailedRetries());
        job.setFailedInterval(msg.getFailedInterval());
        job.setRejectAttempts(msg.getRejectRetries());
        job.setRejectInterval(msg.getRejectInterval());
        job.setUpdateUser(msg.getUser());
        job.setWorkerGroupId(msg.getGroupId());
        Date currentTime = new Date();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
        dateTimeFormat.format(currentTime);
        job.setUpdateTime(currentTime);
        job.setParams(JsonHelper.parseMapEntryList2JSON(msg.getParametersList()));
        return job;
    }

}
