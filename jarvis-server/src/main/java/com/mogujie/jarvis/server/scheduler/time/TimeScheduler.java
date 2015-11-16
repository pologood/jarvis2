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

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.dao.CrontabMapper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Crontab;
import com.mogujie.jarvis.dto.CrontabExample;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.server.scheduler.CronScheduler;
import com.mogujie.jarvis.server.scheduler.JobScheduleException;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.service.CrontabService;
import com.mogujie.jarvis.server.service.JobService;

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
    private CrontabService cronService;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private JobService jobService;

    private CronScheduler cronScheduler = new CronScheduler(getSchedulerController());

    @Override
    @Transactional
    public void init() {
        List<Job> activeJobs = jobService.getActiveJobs();
        Set<Long> jobIds = new HashSet<>();
        for (Job job : activeJobs) {
            Integer fixedDelay = job.getFixedDelay();
            if (fixedDelay != null ) {
                cronScheduler.scheduleOnce(job.getJobId(), fixedDelay);
            } else {
                jobIds.add(job.getJobId());
            }
        }

        CrontabExample crontabExample = new CrontabExample();
        List<Crontab> crontabs = crontabMapper.selectByExample(crontabExample);
        for (Crontab crontab : crontabs) {
            if (jobIds.contains(crontab.getJobId())) {
                cronScheduler.schedule(crontab);
            }
        }

        cronScheduler.start();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void handleStopEvent(StopEvent event) {
        cronScheduler.shutdown();
    }

    @Override
    public void handleStartEvent(StartEvent event) {
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleSuccessEvent(SuccessEvent event) {
        long jobId = event.getJobId();
        Job job = jobMapper.selectByPrimaryKey(jobId);
        Integer fixedDelay = job.getFixedDelay();
        if (fixedDelay != null) {
            cronScheduler.remove(jobId);
            DateTime nextScheduleTime = DateTime.now().plusSeconds(fixedDelay);
            int second = nextScheduleTime.getSecondOfMinute();
            int minute = nextScheduleTime.getMinuteOfHour();
            int hour = nextScheduleTime.getHourOfDay();
            int day = nextScheduleTime.getDayOfMonth();
            int month = nextScheduleTime.getMonthOfYear();
            int year = nextScheduleTime.getYear();
            String[] tokens = { String.valueOf(second), String.valueOf(minute), String.valueOf(hour), String.valueOf(day), String.valueOf(month), "?",
                    String.valueOf(year) };

            Crontab crontab = new Crontab();
            crontab.setJobId(jobId);
            crontab.setCronExpression(Joiner.on(" ").join(tokens));
            cronScheduler.schedule(crontab);
        }
    }

    public void addJob(long jobId) throws JobScheduleException {
        List<Crontab> crontabs = cronService.getCronsByJobId(jobId);
        for (Crontab crontab : crontabs) {
            cronScheduler.schedule(crontab);
        }
    }

    public void removeJob(long jobId) throws JobScheduleException {
        cronScheduler.remove(jobId);
    }

    public void modifyJob(long jobId) throws JobScheduleException {
        cronScheduler.remove(jobId);

        List<Crontab> crontabs = cronService.getCronsByJobId(jobId);
        for (Crontab crontab : crontabs) {
            cronScheduler.schedule(crontab);
        }
    }

    public void modifyJobFlag(long jobId, JobFlag jobFlag) throws JobScheduleException {
        switch (jobFlag) {
            case ENABLE:
                List<Crontab> crontabs = cronService.getCronsByJobId(jobId);
                for (Crontab crontab : crontabs) {
                    cronScheduler.schedule(crontab);
                }
                break;
            default:
                cronScheduler.remove(jobId);
                break;
        }
    }
}
