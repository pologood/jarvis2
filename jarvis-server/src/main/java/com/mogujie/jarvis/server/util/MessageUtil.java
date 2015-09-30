/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月29日 下午4:42:28
 */

package com.mogujie.jarvis.server.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Crontab;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.server.domain.MODIFY_JOB_TYPE;
import com.mogujie.jarvis.server.domain.MODIFY_OPERATION;
import com.mogujie.jarvis.server.domain.ModifyJobEntry;
import com.mogujie.jarvis.server.service.CrontabService;
import com.mogujie.jarvis.server.service.JobService;

/**
 * @author guangming
 *
 */
public class MessageUtil {
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
        job.setFixedDelay(msg.getFixedDelay());
        return job;
    }

    public static Job convert2Job(JobMapper jobMapper, RestServerModifyJobRequest msg) {
        long jobId = msg.getJobId();
        Job job = jobMapper.selectByPrimaryKey(jobId);
        job.setJobId(msg.getJobId());
        if (msg.hasCommand()) {
            job.setContent(msg.getCommand());
        }
        if (msg.hasPriority()) {
            job.setPriority(msg.getPriority());
        }
        if (msg.hasStartTime()) {
            job.setActiveStartDate(new Date(msg.getStartTime()));
        }
        if (msg.hasEndTime()) {
            job.setActiveEndDate(new Date(msg.getEndTime()));
        }
        if (msg.hasFailedRetries()) {
            job.setFailedAttempts(msg.getFailedRetries());
        }
        if (msg.hasFailedInterval()) {
            job.setFailedInterval(msg.getFailedInterval());
        }
        if (msg.hasRejectRetries()) {
            job.setRejectAttempts(msg.getRejectRetries());
        }
        if (msg.hasRejectInterval()) {
            job.setRejectInterval(msg.getRejectInterval());
        }
        if (msg.hasUser()) {
            job.setUpdateUser(msg.getUser());
        }
        if (msg.hasGroupId()) {
            job.setWorkerGroupId(msg.getGroupId());
        }
        Date currentTime = new Date();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
        dateTimeFormat.format(currentTime);
        job.setUpdateTime(currentTime);
        // TODO job.setParams(JsonHelper.parseMapEntryList2JSON(msg.getParametersList()));
        return job;
    }

    public static JobDepend convert2JobDepend(Long jobId, DependencyEntry entry, String user) {
        JobDepend jobDepend = new JobDepend();
        jobDepend.setJobId(jobId);
        jobDepend.setPreJobId(entry.getJobId());
        Date currentTime = new Date();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
        dateTimeFormat.format(currentTime);
        jobDepend.setCreateTime(currentTime);
        jobDepend.setUpdateTime(currentTime);
        jobDepend.setUpdateUser(user);
        jobDepend.setCommonStrategy(entry.getCommonDependStrategy());
        jobDepend.setOffsetStrategy(entry.getLastDependStrategy());
        return jobDepend;
    }

    public static Map<MODIFY_JOB_TYPE, ModifyJobEntry> convert2ModifyJobMap(RestServerModifyJobRequest msg,
            JobService jobService, CrontabService cronService) {
        Map<MODIFY_JOB_TYPE, ModifyJobEntry> modifyMap = new HashMap<MODIFY_JOB_TYPE, ModifyJobEntry>();
        long jobId = msg.getJobId();
        if (msg.hasCronExpression()) {
            String newCronExpression = msg.getCronExpression();
            MODIFY_OPERATION operation;
            if (newCronExpression == null || newCronExpression.isEmpty()) {
                operation = MODIFY_OPERATION.DEL;
            } else {
                Crontab oldCron = cronService.getPositiveCrontab(jobId);
                if (oldCron == null) {
                    operation = MODIFY_OPERATION.ADD;
                } else {
                    operation = MODIFY_OPERATION.MODIFY;
                }
            }
            ModifyJobEntry entry = new ModifyJobEntry(operation, newCronExpression);
            modifyMap.put(MODIFY_JOB_TYPE.CRON, entry);
        }
        if (msg.hasFixedDelay()) {
            int newFixedDelay = msg.getFixedDelay();
            MODIFY_OPERATION operation;
            if (newFixedDelay <=0) {
                operation = MODIFY_OPERATION.DEL;
            } else {
                boolean hasFixedDelay = jobService.hasFixedDelay(jobId);
                if (!hasFixedDelay) {
                    operation = MODIFY_OPERATION.ADD;
                } else {
                    operation = MODIFY_OPERATION.MODIFY;
                }
            }
            ModifyJobEntry entry = new ModifyJobEntry(operation, newFixedDelay);
            modifyMap.put(MODIFY_JOB_TYPE.CYCLE, entry);
        }

        return modifyMap;
    }
}
