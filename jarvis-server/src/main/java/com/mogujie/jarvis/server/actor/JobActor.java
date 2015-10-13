/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 上午10:15:14
 */

package com.mogujie.jarvis.server.actor;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.dao.JobDependMapper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.dto.JobDependKey;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos.DependencyEntry.DependencyOperator;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos.RestServerModifyDependencyRequest;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.RestServerModifyJobFlagRequest;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.server.domain.ModifyDependEntry;
import com.mogujie.jarvis.server.domain.ModifyJobEntry;
import com.mogujie.jarvis.server.domain.ModifyJobType;
import com.mogujie.jarvis.server.domain.ModifyOperation;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.dag.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;
import com.mogujie.jarvis.server.scheduler.dag.DAGScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;
import com.mogujie.jarvis.server.service.CrontabService;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.MessageUtil;

import akka.actor.UntypedActor;

/**
 * @author guangming
 *
 */
@Named("jobActor")
@Scope("prototype")
public class JobActor extends UntypedActor {

    @Autowired
    private DAGScheduler dagScheduler;

    @Autowired
    private TimeScheduler timeScheduler;

    @Autowired
    private CrontabService cronService;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private JobDependMapper jobDependMapper;

    @Override
    public void onReceive(Object obj) throws Exception {
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
                JobDepend jobDepend = MessageUtil.convert2JobDepend(jobId, entry.getJobId(), entry.getCommonDependStrategy(),
                        entry.getLastDependStrategy(), msg.getUser());
                jobDependMapper.insert(jobDepend);
            }

            // 4. construct AddJobEvent
            int cycleFlag = msg.hasFixedDelay() ? 1 : 0;
            int dependFlag = (!needDependencies.isEmpty()) ? 1 : 0;
            int timeFlag = msg.hasCronExpression() ? 1 : 0;
            DAGJobType type = SchedulerUtil.getDAGJobType(cycleFlag, dependFlag, timeFlag);
            try {
                dagScheduler.addJob(jobId, new DAGJob(jobId, type), needDependencies);
                timeScheduler.addJob(jobId);
                getSender().tell("sucess", getSelf());
            } catch (Exception e) {
                getSender().tell(e.getMessage(), getSelf());
            }
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
            Map<ModifyJobType, ModifyJobEntry> modifyMap = MessageUtil.convert2ModifyJobMap(msg, jobService, cronService);
            try {
                dagScheduler.modifyDAGJobType(jobId, modifyMap);
                timeScheduler.modifyJob(jobId);
                getSender().tell("sucess", getSelf());
            } catch (Exception e) {
                getSender().tell(e.getMessage(), getSelf());
            }
        } else if (obj instanceof RestServerModifyDependencyRequest) {
            RestServerModifyDependencyRequest msg = (RestServerModifyDependencyRequest) obj;
            long jobId = msg.getJobId();
            List<ModifyDependEntry> dependEntries = new ArrayList<ModifyDependEntry>();
            for (com.mogujie.jarvis.protocol.ModifyDependencyProtos.DependencyEntry entry : msg.getDependencyEntryList()) {
                long preJobId = entry.getJobId();
                int commonStrategyValue = entry.getCommonDependStrategy();
                String offsetStrategyValue = entry.getLastDependStrategy();
                // TODO
                String user = null;
                ModifyOperation operation;
                if (entry.getOperator().equals(DependencyOperator.ADD)) {
                    operation = ModifyOperation.ADD;
                    JobDepend jobDepend = MessageUtil.convert2JobDepend(jobId, preJobId, entry.getCommonDependStrategy(),
                            entry.getLastDependStrategy(), user);
                    jobDependMapper.insert(jobDepend);
                } else if (entry.getOperator().equals(DependencyOperator.REMOVE)) {
                    operation = ModifyOperation.DEL;
                    JobDependKey key = new JobDependKey();
                    key.setJobId(jobId);
                    key.setPreJobId(preJobId);
                    jobDependMapper.deleteByPrimaryKey(key);
                } else {
                    operation = ModifyOperation.MODIFY;
                    JobDependKey key = new JobDependKey();
                    key.setJobId(jobId);
                    key.setPreJobId(preJobId);
                    JobDepend record = jobDependMapper.selectByPrimaryKey(key);
                    if (record != null) {
                        record.setCommonStrategy(commonStrategyValue);
                        record.setOffsetStrategy(offsetStrategyValue);
                        record.setUpdateUser(user);
                        Date currentTime = new Date();
                        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
                        dateTimeFormat.format(currentTime);
                        record.setUpdateTime(currentTime);
                        jobDependMapper.updateByPrimaryKey(record);
                    }
                }
                ModifyDependEntry dependEntry = new ModifyDependEntry(operation, preJobId, commonStrategyValue, offsetStrategyValue);
                dependEntries.add(dependEntry);
            }
            try {
                dagScheduler.modifyDependency(jobId, dependEntries);
                getSender().tell("sucess", getSelf());
            } catch (Exception e) {
                getSender().tell(e.getMessage(), getSelf());
            }
        } else if (obj instanceof RestServerModifyJobFlagRequest) {
            RestServerModifyJobFlagRequest msg = (RestServerModifyJobFlagRequest) obj;
            long jobId = msg.getJobId();
            JobFlag flag = JobFlag.getInstance(msg.getJobFlag());
            try {
                dagScheduler.modifyJobFlag(jobId, flag);
                timeScheduler.modifyJobFlag(jobId, flag);
                getSender().tell("sucess", getSelf());
            } catch (Exception e) {
                getSender().tell(e.getMessage(), getSelf());
            }
        } else {
            unhandled(obj);
        }
    }

    public static Set<Class<?>> handledMessages() {
        Set<Class<?>> set = new HashSet<>();
        set.add(RestServerSubmitJobRequest.class);
        set.add(RestServerModifyJobRequest.class);
        set.add(RestServerModifyJobFlagRequest.class);
        return set;
    }
}
