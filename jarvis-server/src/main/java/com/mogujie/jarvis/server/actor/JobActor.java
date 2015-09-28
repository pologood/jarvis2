/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 上午10:15:14
 */

package com.mogujie.jarvis.server.actor;

import java.text.DateFormat;
import java.util.Date;
import java.util.Set;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import akka.actor.UntypedActor;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.dao.JobDependMapper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.RestServerModifyJobFlagRequest;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.server.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.JobScheduleType;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.event.AddJobEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobEvent.MODIFY_TYPE;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobFlagEvent;
import com.mogujie.jarvis.server.scheduler.event.UnhandleEvent;
import com.mogujie.jarvis.server.service.CrontabService;
import com.mogujie.jarvis.server.service.JobDependService;

/**
 * @author guangming
 *
 */
@Named("jobActor")
@Scope("prototype")
public class JobActor extends UntypedActor {

    @Autowired
    private JobSchedulerController schedulerController;

    @Autowired
    private CrontabService cronService;

    @Autowired
    private JobDependService jobDependService;

    @Autowired
    JobMapper jobMapper;

    @Autowired
    JobDependMapper jobDependMapper;

    @Override
    public void onReceive(Object obj) throws Exception {
        Event event = new UnhandleEvent();
        //TODO
        if (obj instanceof RestServerSubmitJobRequest) {
            RestServerSubmitJobRequest msg = (RestServerSubmitJobRequest) obj;
            // 1. insert job to DB
            Job job = SchedulerUtil.convert2Job(msg);
            jobMapper.insert(job);
            long jobId = job.getJobId();
            // 如果是新增任务（不是手动触发），则originId=jobId
            if (job.getOriginJobId() == null || job.getOriginJobId() == 0) {
                job.setOriginJobId(jobId);
                jobMapper.updateByPrimaryKey(job);
            }
            Set<Long> needDependencies = Sets.newHashSet();
            if (msg.getDependencyJobidsList() != null) {
                needDependencies.addAll(msg.getDependencyJobidsList());
            }
            // 2. insert cron to DB
            cronService.insert(jobId, msg.getCronExpression());
            // 3. insert jobDepend to DB
            for (long d : needDependencies) {
                JobDepend jobDepend = new JobDepend();
                jobDepend.setJobId(jobId);
                jobDepend.setPreJobId(d);
                Date currentTime = new Date();
                DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
                dateTimeFormat.format(currentTime);
                jobDepend.setCreateTime(currentTime);
                jobDepend.setUpdateTime(currentTime);
                jobDepend.setUpdateUser(msg.getUser());
                jobDependMapper.insert(jobDepend);
            }
            // 4. get jobScheduleType
            boolean hasCron = (msg.getCronExpression() != null);
            boolean hasDepend = (!needDependencies.isEmpty());
            JobScheduleType type = SchedulerUtil.getJobScheduleType(hasCron, hasDepend);
            event = new AddJobEvent(jobId, needDependencies, type);
        } else if (obj instanceof RestServerModifyJobRequest) {
            RestServerModifyJobRequest msg = (RestServerModifyJobRequest) obj;
            long jobId = msg.getJobId();
            // 1. update job to DB
            Job job = SchedulerUtil.convert2Job(msg);
            jobMapper.updateByPrimaryKey(job);
            // 2. update cron to DB
            cronService.update(jobId, msg.getCronExpression());
            // 3. update jobDepend to DB
            Set<Long> needDependencies = Sets.newHashSet();
            if (msg.getDependencyJobidsList() != null) {
                needDependencies.addAll(msg.getDependencyJobidsList());
            }
            jobDependService.deleteByJobId(jobId);
            for (long d : needDependencies) {
                JobDepend jobDepend = new JobDepend();
                jobDepend.setJobId(jobId);
                jobDepend.setPreJobId(d);
                Date currentTime = new Date();
                DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
                dateTimeFormat.format(currentTime);
                jobDepend.setUpdateTime(currentTime);
                jobDepend.setUpdateUser(msg.getUser());
                jobDependMapper.insert(jobDepend);
            }
            boolean hasCron = (msg.getCronExpression() != null);
            event = new ModifyJobEvent(jobId, needDependencies, MODIFY_TYPE.MODIFY, hasCron);
        } else if (obj instanceof RestServerModifyJobFlagRequest) {
            RestServerModifyJobFlagRequest msg = (RestServerModifyJobFlagRequest) obj;
            long jobId = msg.getJobId();
            JobFlag flag = JobFlag.getInstance(msg.getJobFlag());
            event = new ModifyJobFlagEvent(jobId, flag);
        } else {
            unhandled(obj);
        }

        schedulerController.notify(event);
    }
}
