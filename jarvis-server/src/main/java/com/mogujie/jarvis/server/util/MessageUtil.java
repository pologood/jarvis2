/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月29日 下午4:42:28
 */

package com.mogujie.jarvis.server.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Crontab;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.server.domain.ModifyJobEntry;
import com.mogujie.jarvis.server.domain.ModifyJobType;
import com.mogujie.jarvis.server.domain.ModifyOperation;
import com.mogujie.jarvis.server.service.AppService;
import com.mogujie.jarvis.server.service.CrontabService;
import com.mogujie.jarvis.server.service.JobService;

/**
 * @author guangming
 *
 */
public class MessageUtil {
    public static Job convert2Job(AppService appService, RestServerSubmitJobRequest msg) {
        Job job = new Job();
        job.setAppId(appService.getAppIdByName(msg.getAppName()));
        job.setJobName(msg.getJobName());
        job.setContent(msg.getContent());
        job.setPriority(msg.getPriority());
        job.setJobFlag(JobFlag.ENABLE.getValue());
        job.setJobType(msg.getJobType());

        if(msg.getStartTime() != 0){
            job.setActiveStartDate(new Date(msg.getStartTime()));
        }
        if(msg.getEndTime() != 0){
            job.setActiveEndDate(new Date(msg.getEndTime()));
        }

        job.setFailedAttempts(msg.getFailedRetries());
        job.setFailedInterval(msg.getFailedInterval());
        job.setRejectAttempts(msg.getRejectRetries());
        job.setRejectInterval(msg.getRejectInterval());
        job.setSubmitUser(msg.getUser());
        job.setUpdateUser(msg.getUser());
        job.setWorkerGroupId(msg.getGroupId());
        job.setOriginJobId(msg.getOriginJobId());
        job.setFixedDelay(msg.getFixedDelay());
        if (msg.getParametersList() != null && msg.getParametersList().size() != 0) {
            job.setParams(JsonHelper.parseMapEntryList2JSON(msg.getParametersList()));
        }
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        job.setCreateTime(currentTime);
        job.setUpdateTime(currentTime);
        return job;
    }

    public static Job convert2Job(JobMapper jobMapper, AppService appService,
            RestServerModifyJobRequest msg) {
        long jobId = msg.getJobId();
        Job job = jobMapper.selectByPrimaryKey(jobId);
        job.setJobId(msg.getJobId());
        if (msg.hasAppName()) {
            job.setAppId(appService.getAppIdByName(msg.getAppName()));
        }
        if (msg.hasContent()) {
            job.setContent(msg.getContent());
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
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        job.setUpdateTime(currentTime);
        // TODO job.setParams(JsonHelper.parseMapEntryList2JSON(msg.getParametersList()));
        return job;
    }

    public static JobDepend convert2JobDepend(Long jobId, long preJobId, int commonStrategyValue, String offsetStrategyValue, String user) {
        JobDepend jobDepend = new JobDepend();
        jobDepend.setJobId(jobId);
        jobDepend.setPreJobId(preJobId);
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        jobDepend.setCreateTime(currentTime);
        jobDepend.setUpdateTime(currentTime);
        jobDepend.setUpdateUser(user);
        jobDepend.setCommonStrategy(commonStrategyValue);
        jobDepend.setOffsetStrategy(offsetStrategyValue);
        return jobDepend;
    }

    public static Map<ModifyJobType, ModifyJobEntry> convert2ModifyJobMap(RestServerModifyJobRequest msg, JobService jobService,
            CrontabService cronService) {
        Map<ModifyJobType, ModifyJobEntry> modifyMap = new HashMap<ModifyJobType, ModifyJobEntry>();
        long jobId = msg.getJobId();
        if (msg.hasCronExpression()) {
            String newCronExpression = msg.getCronExpression();
            ModifyOperation operation;
            if (newCronExpression == null || newCronExpression.isEmpty()) {
                operation = ModifyOperation.DEL;
            } else {
                Crontab oldCron = cronService.getPositiveCrontab(jobId);
                if (oldCron == null) {
                    operation = ModifyOperation.ADD;
                } else {
                    operation = ModifyOperation.MODIFY;
                }
            }
            ModifyJobEntry entry = new ModifyJobEntry(operation, newCronExpression);
            modifyMap.put(ModifyJobType.CRON, entry);
        }
        if (msg.hasFixedDelay()) {
            int newFixedDelay = msg.getFixedDelay();
            ModifyOperation operation;
            if (newFixedDelay <= 0) {
                operation = ModifyOperation.DEL;
            } else {
                boolean hasFixedDelay = jobService.hasFixedDelay(jobId);
                if (!hasFixedDelay) {
                    operation = ModifyOperation.ADD;
                } else {
                    operation = ModifyOperation.MODIFY;
                }
            }
            ModifyJobEntry entry = new ModifyJobEntry(operation, newFixedDelay);
            modifyMap.put(ModifyJobType.CYCLE, entry);
        }

        return modifyMap;
    }
}
