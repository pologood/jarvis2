/*
 * 蘑菇街 Inc. Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming Create Date: 2015年9月29日 下午4:42:28
 */

package com.mogujie.jarvis.server.service;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.mogujie.jarvis.core.domain.AppType;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.dto.generate.App;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.RestServerSubmitTaskRequest;
import com.mogujie.jarvis.server.domain.JobEntry;

/**
 * @author guangming
 *
 */

@Service
public class ConvertValidService {

    @Autowired
    private AppService appService;

    @Autowired
    private JobService jobService;

    public Job convert2Job(RestSubmitJobRequest msg) {
        Job job = new Job();

        Integer appId;
        App appAuth = appService.getAppByName(msg.getAppAuth().getName());
        if (appAuth.getAppType() == AppType.NORMAL.getValue()) {
            appId = appAuth.getAppId();
        } else {
            if (msg.hasAppName()) {
                appId = appService.getAppIdByName(msg.getAppName());
            } else {
                appId = appAuth.getAppId();
            }
        }
        job.setAppId(appId);

        job.setAppId(appService.getAppIdByName(msg.getAppAuth().getName()));

        job.setJobName(msg.getJobName());
        job.setContent(msg.getContent());
        job.setParams(msg.getParameters());
        job.setPriority(msg.getPriority());
        job.setJobFlag(msg.getJobFlag());
        job.setJobType(msg.getJobType());
        job.setWorkerGroupId(msg.getWorkerGroupId());

        if (msg.hasActiveStartTime() && msg.getActiveStartTime() != 0) {
            job.setActiveStartDate(new Date(msg.getActiveStartTime()));
        }
        if (msg.hasActiveEndTime() && msg.getActiveEndTime() != 0) {
            job.setActiveEndDate(new Date(msg.getActiveEndTime()));
        }

        job.setRejectAttempts(msg.getRejectAttempts());
        job.setRejectInterval(msg.getRejectInterval());
        job.setFailedAttempts(msg.getFailedAttempts());
        job.setFailedInterval(msg.getFailedInterval());
        job.setSubmitUser(msg.getUser());
        job.setUpdateUser(msg.getUser());

        Date currentTime = DateTime.now().toDate();
        job.setCreateTime(currentTime);
        job.setUpdateTime(currentTime);

        //valid
        Preconditions.checkArgument(job.getJobName() != null && !job.getJobName().isEmpty(), "jobName不能为空");
        Preconditions.checkArgument(job.getWorkerGroupId() != null && job.getWorkerGroupId() > 0, "workGroupId不能为空");
        Preconditions.checkArgument(job.getContent() != null && !job.getContent().isEmpty(), "job内容不能为空");
        Preconditions.checkArgument(appService.canAccessWorkerGroup(job.getAppId(), job.getWorkerGroupId()), "该App不能访问指定的workerGroupId.");
        if (job.getActiveStartDate() != null && job.getActiveEndDate() != null) {
            Preconditions.checkArgument(job.getActiveStartDate().getTime() <= job.getActiveEndDate().getTime(), "有效开始日不能大于有效结束日");
        }

        return job;
    }

    public Job convert2Job(RestModifyJobRequest msg) {
        Job job = new Job();
        long jobId = msg.getJobId();
        Preconditions.checkArgument(jobId != 0, "jobId不能为空");
        JobEntry jobEntry = jobService.get(jobId);
        Preconditions.checkArgument(jobEntry != null, "该job不存在");

        Integer appId;
        App appAuth = appService.getAppByName(msg.getAppAuth().getName());
        if (appAuth.getAppType() == AppType.NORMAL.getValue()) {
            appId = appAuth.getAppId();
        } else {
            if (msg.hasAppName()) {
                appId = appService.getAppIdByName(msg.getAppName());
            } else {
                appId = appAuth.getAppId();
            }
        }
        job.setAppId(appId);
        job.setJobId(msg.getJobId());
        if (msg.hasJobName()) {
            job.setJobName(msg.getJobName());
        }
        if (msg.hasJobType()) {
            job.setJobType(msg.getJobType());
        }
        if (msg.hasJobFlag()) {
            job.setJobFlag(msg.getJobFlag());
        }
        if (msg.hasContent()) {
            job.setContent(msg.getContent());
        }
        if (msg.hasParameters()) {
            job.setParams(msg.getParameters());
        }
        if (msg.hasWorkerGroupId()) {
            job.setWorkerGroupId(msg.getWorkerGroupId());
        }
        if (msg.hasPriority()) {
            job.setPriority(msg.getPriority());
        }
        if (msg.hasActiveStartTime()) {
            job.setActiveStartDate(new Date(msg.getActiveStartTime()));
        }
        if (msg.hasActiveEndTime()) {
            job.setActiveEndDate(new Date(msg.getActiveEndTime()));
        }
        if (msg.hasRejectAttempts()) {
            job.setRejectAttempts(msg.getRejectAttempts());
        }
        if (msg.hasRejectInterval()) {
            job.setRejectInterval(msg.getRejectInterval());
        }
        if (msg.hasFailedAttempts()) {
            job.setFailedAttempts(msg.getFailedAttempts());
        }
        if (msg.hasFailedInterval()) {
            job.setFailedInterval(msg.getFailedInterval());
        }

        job.setUpdateUser(msg.getUser());
        Date currentTime = DateTime.now().toDate();
        job.setUpdateTime(currentTime);
        return job;
    }

    public JobDepend convert2JobDepend(Long jobId, DependencyEntry entry, String user) {
        JobDepend jobDepend = new JobDepend();
        jobDepend.setJobId(jobId);
        jobDepend.setPreJobId(entry.getJobId());
        jobDepend.setCommonStrategy(entry.getCommonDependStrategy());
        jobDepend.setOffsetStrategy(entry.getOffsetDependStrategy());
        Date currentTime = DateTime.now().toDate();
        jobDepend.setCreateTime(currentTime);
        jobDepend.setUpdateTime(currentTime);
        jobDepend.setUpdateUser(user);
        return jobDepend;
    }

    public Task convert2Task(RestServerSubmitTaskRequest msg) {
        Task task = new Task();
        task.setJobId((long) 0);
        task.setAttemptId(1);
        task.setContent(msg.getContent());
        task.setExecuteUser(msg.getUser());
        task.setProgress((float) 0);
        Date now = new Date();
        task.setScheduleTime(now);
        task.setCreateTime(now);
        task.setUpdateTime(now);
        task.setParams(msg.getParameters());
        task.setStatus(TaskStatus.WAITING.getValue());
        int appId = appService.getAppIdByName(msg.getAppAuth().getName());
        task.setAppId(appId);
        return task;
    }

}
