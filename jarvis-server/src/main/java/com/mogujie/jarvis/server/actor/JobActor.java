/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 上午10:15:14
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

import akka.actor.UntypedActor;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.dao.JobDependMapper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos.DependencyEntry.DependencyOperator;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos.RestServerModifyDependencyRequest;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.RestServerModifyJobFlagRequest;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.server.domain.ModifyDependEntry;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;
import com.mogujie.jarvis.server.scheduler.event.AddJobEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyDependencyEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobEvent;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobFlagEvent;
import com.mogujie.jarvis.server.scheduler.event.UnhandleEvent;
import com.mogujie.jarvis.server.service.CrontabService;
import com.mogujie.jarvis.server.service.JobDependService;
import com.mogujie.jarvis.server.util.MessageUtil;

/**
 * @author guangming
 *
 */
@Named("jobActor")
@Scope("prototype")
public class JobActor extends UntypedActor {

    @Autowired
    @Qualifier("AsyncSchedulerController")
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
        // TODO
        if (obj instanceof RestServerSubmitJobRequest) {
            RestServerSubmitJobRequest msg = (RestServerSubmitJobRequest) obj;
            Set<Long> needDependencies = Sets.newHashSet();
            // 1. insert job to DB
            Job job = MessageUtil.convert2Job(msg);
            jobMapper.insert(job);
            long jobId = job.getJobId();
            // 如果是新增任务（不是手动触发），则originId=jobId
            if (job.getOriginJobId() == null || job.getOriginJobId() == 0) {
                job.setOriginJobId(jobId);
                jobMapper.updateByPrimaryKey(job);
            }

            // 2. insert cron to DB
            cronService.insert(jobId, msg.getCronExpression());

            // 3. insert jobDepend to DB
            for (DependencyEntry entry : msg.getDependencyEntryList()) {
                needDependencies.add(entry.getJobId());
                JobDepend jobDepend = MessageUtil.convert2JobDepend(jobId, entry, msg.getUser());
                jobDependMapper.insert(jobDepend);
            }

            // 4. construct AddJobEvent
            int cycleFlag = msg.hasFixedDelay() ? 1 : 0;
            int dependFlag = (!needDependencies.isEmpty()) ? 1 : 0;
            int timeFlag = msg.hasCronExpression() ? 1 : 0;
            DAGJobType type = SchedulerUtil.getDAGJobType(cycleFlag, dependFlag, timeFlag);
            event = new AddJobEvent(jobId, needDependencies, type);
        } else if (obj instanceof RestServerModifyJobRequest) {
            RestServerModifyJobRequest msg = (RestServerModifyJobRequest) obj;
            long jobId = msg.getJobId();
            // 1. update job to DB
            Job job = MessageUtil.convert2Job(jobMapper, msg);
            jobMapper.updateByPrimaryKey(job);

            // 2. update cron to DB
            if (msg.hasCronExpression()) {
                cronService.updateOrDelete(jobId, msg.getCronExpression());
            }

            // 3. construct ModifyJobEvent
            boolean hasCron = (cronService.getPositiveCrontab(jobId) != null);
            boolean hasCycle = (job.getFixedDelay() > 0);
            event = new ModifyJobEvent(jobId, hasCron, hasCycle);
        } else if (obj instanceof RestServerModifyDependencyRequest) {
            RestServerModifyDependencyRequest msg = (RestServerModifyDependencyRequest) obj;
            long jobId = msg.getJobId();
            List<ModifyDependEntry> dependEntries =  new ArrayList<ModifyDependEntry>();
            for (com.mogujie.jarvis.protocol.ModifyDependencyProtos.DependencyEntry entry
                    : msg.getDependencyEntryList()) {
                ModifyDependEntry.MODIFY_OPERATION operation =
                        entry.getOperator().equals(DependencyOperator.ADD) ?
                        ModifyDependEntry.MODIFY_OPERATION.ADD :
                        ModifyDependEntry.MODIFY_OPERATION.DEL;
                ModifyDependEntry dependEntry = new ModifyDependEntry(operation, entry.getJobId());
                dependEntries.add(dependEntry);
            }
            event = new ModifyDependencyEvent(jobId, dependEntries);
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

    public static Set<Class<?>> handledMessages() {
        Set<Class<?>> set = new HashSet<>();
        set.add(RestServerSubmitJobRequest.class);
        set.add(RestServerModifyJobRequest.class);
        set.add(RestServerModifyJobFlagRequest.class);
        return set;
    }
}
