/*
 * 蘑菇街 Inc. Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming Create Date: 2015年9月29日 下午4:42:28
 */

package com.mogujie.jarvis.server.service;

import java.util.Date;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.AppType;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.dto.generate.App;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobStatusRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.RestServerSubmitTaskRequest;
import com.mogujie.jarvis.server.domain.JobEntry;

/**
 * @author guangming
 */

@Singleton
public class ConvertValidService {

    private enum CheckMode {
        ADD, //追加
        EDIT, //修改
        EDIT_STATUS, //修改_状态
        DELETE //删除
    }

    @Inject
    private AppService appService;

    @Inject
    private JobService jobService;

    public Job convertCheck2Job(RestSubmitJobRequest msg) {
        Job job = convert2Job(msg);
        checkJob(CheckMode.ADD, job);
        return job;
    }

    public Job convertCheck2Job(RestModifyJobRequest msg) {
        Job job = convert2Job(msg);
        checkJob(CheckMode.EDIT, job);
        return job;
    }

    public Job convertCheck2Job(RestModifyJobStatusRequest msg) {
        Job job = convert2Job(msg);
        checkJob(CheckMode.EDIT_STATUS, job);
        return job;
    }

    public Job convert2Job(RestSubmitJobRequest msg) {
        Job job = new Job();
        job.setAppId(dealAppId(msg.getAppAuth(), msg.getAppName()));
        job.setJobName(msg.getJobName());
        job.setContent(msg.getContent());
        job.setParams(msg.getParameters());
        job.setPriority(msg.getPriority());
        job.setStatus(msg.getStatus());
        job.setJobType(msg.getJobType());
        job.setWorkerGroupId(msg.getWorkerGroupId());
        if (msg.hasActiveStartTime() && msg.getActiveStartTime() != 0) {
            job.setActiveStartDate(new DateTime(msg.getActiveStartTime()).toDate());
        } else {
            job.setActiveStartDate(JarvisConstants.DATETIME_MIN.toDate());
        }
        if (msg.hasActiveEndTime() && msg.getActiveEndTime() != 0) {
            job.setActiveEndDate(new DateTime(msg.getActiveEndTime()).toDate());
        } else {
            job.setActiveEndDate(JarvisConstants.DATETIME_MAX.toDate());
        }

        job.setExpiredTime(msg.getExpiredTime());
        job.setFailedAttempts(msg.getFailedAttempts());
        job.setFailedInterval(msg.getFailedInterval());
        job.setSubmitUser(msg.getUser());
        job.setUpdateUser(msg.getUser());

        DateTime now = DateTime.now();
        job.setCreateTime(now.toDate());
        job.setUpdateTime(now.toDate());

        return job;
    }

    public Job convert2Job(RestModifyJobRequest msg) {
        Job job = new Job();
        job.setJobId(msg.getJobId());
        job.setAppId(dealAppId(msg.getAppAuth(), msg.getAppName()));
        if (msg.hasJobName()) {
            job.setJobName(msg.getJobName());
        }
        if (msg.hasJobType()) {
            job.setJobType(msg.getJobType());
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
        if (msg.hasExpiredTime()) {
            job.setExpiredTime(msg.getExpiredTime());
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

    public Job convert2Job(RestModifyJobStatusRequest msg) {
        Job job = new Job();
        job.setJobId(msg.getJobId());
        job.setStatus(msg.getStatus());
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

    /**
     * @param job
     */
    public void checkJob(CheckMode mode, Job job) {

        //为空检查
        if (mode == CheckMode.ADD) {
            Preconditions.checkNotNull(job.getJobName(), "jobName不能为空");
            Preconditions.checkNotNull(job.getWorkerGroupId(), "workGroupId不能为空");
            Preconditions.checkNotNull(job.getContent(), "job内容不能为空");
            Preconditions.checkNotNull(job.getStatus(), "status不能为空");
        }

        Job oldJob = null;
        if (mode == CheckMode.EDIT || mode == CheckMode.EDIT_STATUS) {
            Preconditions.checkArgument(job.getJobId() != null && job.getJobId() != 0, "jobId不能为空");
            JobEntry oldJobEntry = jobService.get(job.getJobId());
            Preconditions.checkNotNull(oldJobEntry, "job不存在");
            oldJob = oldJobEntry.getJob();

            if (mode == CheckMode.EDIT_STATUS) {
                Preconditions.checkNotNull(job.getStatus(), "status不能为空");
            }
        }

        //内容检查
        Preconditions.checkArgument(job.getJobName() == null || !job.getJobName().isEmpty(), "jobName不能为空");
        Preconditions.checkArgument(job.getWorkerGroupId() == null || job.getWorkerGroupId() > 0, "workGroupId不能为空");
        Preconditions.checkArgument(job.getContent() == null || !job.getContent().isEmpty(), "job内容不能为空");
        Preconditions.checkArgument(job.getStatus() == null || JobStatus.isValid(job.getStatus()), "status内容不正确。value:" + job.getStatus());
        if (job.getActiveStartDate() != null && job.getActiveEndDate() != null) {
            Preconditions.checkArgument(job.getActiveStartDate().getTime() <= job.getActiveEndDate().getTime(), "有效开始日不能大于有效结束日");
        } else if (job.getActiveStartDate() != null && job.getActiveEndDate() == null) {
            Preconditions.checkArgument(job.getActiveStartDate().getTime() <= oldJob.getActiveEndDate().getTime(), "有效开始日不能大于有效结束日");
        } else if (job.getActiveStartDate() == null && job.getActiveEndDate() != null) {
            Preconditions.checkArgument(oldJob.getActiveStartDate().getTime() <= job.getActiveEndDate().getTime(), "有效开始日不能大于有效结束日");
        }

    }

    private int dealAppId(AppAuth appAuth, String appName) {
        int appId;
        App app = appService.getAppByName(appAuth.getName());
        if (app.getAppType() == AppType.NORMAL.getValue()) {
            appId = app.getAppId();
        } else {
            if (appName != null) {
                appId = appService.getAppIdByName(appName);
            } else {
                appId = app.getAppId();
            }
        }
        return appId;
    }

}
