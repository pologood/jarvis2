/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 上午10:42:32
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.dao.CrontabMapper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Crontab;
import com.mogujie.jarvis.dto.CrontabExample;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobExample;
import com.mogujie.jarvis.server.scheduler.CronScheduler;
import com.mogujie.jarvis.server.scheduler.JobScheduleException;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;

/**
 * Scheduler used to handle time based job.
 *
 * @author wuya
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
    @Transactional
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
            if (job.getFixedDelay() != null) {
                cronScheduler.scheduleOnce(job.getJobId(), job.getFixedDelay());
            } else {
                jobIds.add(job.getJobId());
            }
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

//    @Subscribe
//    public void handleAddJobEvent(AddJobEvent event) {
//        long jobId = event.getJobId();
//        CrontabExample crontabExample = new CrontabExample();
//        crontabExample.createCriteria().andJobIdEqualTo(jobId);
//
//        List<Crontab> crontabs = crontabMapper.selectByExample(crontabExample);
//        for (Crontab crontab : crontabs) {
//            cronScheduler.schedule(crontab);
//        }
//    }

    public void addJob(long jobId) throws JobScheduleException {
        CrontabExample crontabExample = new CrontabExample();
        crontabExample.createCriteria().andJobIdEqualTo(jobId);

        List<Crontab> crontabs = crontabMapper.selectByExample(crontabExample);
        for (Crontab crontab : crontabs) {
            cronScheduler.schedule(crontab);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleSuccessEvent(SuccessEvent event) {
    }

//    @Subscribe
//    public void handleModifyJobEvent(ModifyJobEvent event) {
//        // TODO handleModifyJobEvent
//    }
    public void modifyJob() throws JobScheduleException {
        // TODO
    }

//    @Subscribe
//    public void handleModifyJobFlagEvent(ModifyJobFlagEvent event) {
//        // TODO handleModifyJobFlagEvent
//    }

    public void modifyJobFlag(long jobId, JobFlag jobFlag) throws JobScheduleException {
        // TODO
    }

}
