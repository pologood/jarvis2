/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:42:32
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.dao.CrontabMapper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Crontab;
import com.mogujie.jarvis.dto.CrontabExample;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobExample;
import com.mogujie.jarvis.server.scheduler.CronScheduler;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.event.AddJobEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobFlagEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;

/**
 * Scheduler used to handle time based job.
 *
 * @author guangming
 *
 */
@Repository
public class TimeScheduler extends Scheduler {
    @Autowired
    private CrontabMapper crontabMapper;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private CronScheduler cronScheduler;

    private static TimeScheduler instance = new TimeScheduler();
    private TimeScheduler() {
    }
    public static TimeScheduler getInstance() {
        return instance;
    }

    @Override
    public void init() {
        getSchedulerController().register(this);

        cronScheduler.start();
        CrontabExample crontabExample = new CrontabExample();
        List<Crontab> crontabs = crontabMapper.selectByExample(crontabExample);
        JobExample jobExample = new JobExample();
        jobExample.createCriteria().andJobFlagEqualTo(JobFlag.ENABLE.getValue());
        List<Job> enableJobs = jobMapper.selectByExample(jobExample);
        Set<Long> jobIds = new HashSet<>();
        for (Job job : enableJobs) {
            jobIds.add(job.getJobId());
        }

        for (Crontab crontab : crontabs) {
            if (jobIds.contains(crontab.getJobId())) {
                cronScheduler.schedule(crontab);
            }
        }
    }

    @Override
    public void destroy() {
        getSchedulerController().unregister(this);
    }

    @Override
    public void handleStopEvent(StopEvent event) {
        cronScheduler.shutdown();
    }

    @Override
    public void handleStartEvent(StartEvent event) {
    }

    @Subscribe
    public void handleAddJobEvent(AddJobEvent event) {
        long jobId = event.getJobId();
        CrontabExample crontabExample = new CrontabExample();
        crontabExample.createCriteria().andJobIdEqualTo(jobId);

        List<Crontab> crontabs = crontabMapper.selectByExample(crontabExample);
        for (Crontab crontab : crontabs) {
            cronScheduler.schedule(crontab);
        }
    }

    @Subscribe
    public void handleModifyJobEvent(ModifyJobEvent event) {
        // TODO handleModifyJobEvent
    }

    @Subscribe
    public void handleModifyJobFlagEvent(ModifyJobFlagEvent event) {
        // TODO handleModifyJobFlagEvent
    }

}
