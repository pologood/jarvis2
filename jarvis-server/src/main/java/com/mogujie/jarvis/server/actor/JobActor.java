/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 上午10:15:14
 */

package com.mogujie.jarvis.server.actor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

import akka.actor.UntypedActor;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.JobRelationType;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.exeception.JobScheduleException;
import com.mogujie.jarvis.core.expression.ScheduleExpressionType;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.JobDependKey;
import com.mogujie.jarvis.dto.generate.JobScheduleExpression;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.JobProtos.JobFlagEntry;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestQueryJobRelationRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerQueryJobRelationResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.domain.ModifyDependEntry;
import com.mogujie.jarvis.server.domain.ModifyOperation;
import com.mogujie.jarvis.server.domain.RemoveJobRequest;
import com.mogujie.jarvis.server.scheduler.dag.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;
import com.mogujie.jarvis.server.scheduler.dag.DAGScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeSchedulerFactory;
import com.mogujie.jarvis.server.service.ConvertValidService;
import com.mogujie.jarvis.server.service.JobService;

/**
 * @author guangming
 */
@Named("jobActor")
@Scope("prototype")
public class JobActor extends UntypedActor {

    private DAGScheduler dagScheduler = DAGScheduler.getInstance();
    private TimeScheduler timeScheduler = TimeSchedulerFactory.getInstance();

    @Autowired
    private JobService jobService;
    @Autowired
    private ConvertValidService convertValidService;

    /**
     * 处理消息
     *
     * @return
     */
    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestSubmitJobRequest.class, ServerSubmitJobResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestModifyJobRequest.class, ServerModifyJobResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestQueryJobRelationRequest.class, ServerQueryJobRelationResponse.class, MessageType.GENERAL));
        return list;
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestSubmitJobRequest) {
            submitJob((RestSubmitJobRequest) obj);
        } else if (obj instanceof RestModifyJobRequest) {
            modifyJob((RestModifyJobRequest) obj);
        } else if (obj instanceof RestQueryJobRelationRequest) {
            RestQueryJobRelationRequest msg = (RestQueryJobRelationRequest) obj;
            queryJobRelation(msg);
        } else if (obj instanceof RemoveJobRequest) { // 测试用，无须加入handleMessage
            RemoveJobRequest msg = (RemoveJobRequest) obj;
            removeJob(msg);
        } else {
            unhandled(obj);
        }
    }

    /**
     * 提交任务
     *
     * @param msg
     * @throws IOException
     */
    @Transactional
    private void submitJob(RestSubmitJobRequest msg) throws Exception {

        ServerSubmitJobResponse response;
        try {
            // 参数检查
            Job job = convertValidService.convert2Job(msg);

            // 1. insert job to DB
            long jobId = jobService.insertJob(job);

            // 2. insert schedule expression to DB
            if (msg.hasExpressionEntry()) {
                jobService.insertScheduleExpression(jobId, msg.getExpressionEntry());
            }

            // 3. insert jobDepend to DB
            Set<Long> needDependencies = Sets.newHashSet();
            for (DependencyEntry entry : msg.getDependencyEntryList()) {
                needDependencies.add(entry.getJobId());
                JobDepend jobDepend = convertValidService.convert2JobDepend(jobId, entry, msg.getUser());
                jobService.insertJobDepend(jobDepend);
            }

            // 4. add job to scheduler
            int timeFlag = 0;
            int cycleFlag = 0;
            if (msg.hasExpressionEntry()) {
                if (msg.getExpressionEntry().getExpressionType() == ScheduleExpressionType.FIXED_DELAY.getValue()) {
                    cycleFlag = 1;
                } else {
                    timeFlag = 1;
                }
            }
            int dependFlag = (!needDependencies.isEmpty()) ? 1 : 0;
            DAGJobType type = DAGJobType.getDAGJobType(cycleFlag, dependFlag, timeFlag);
            dagScheduler.getJobGraph().addJob(jobId, new DAGJob(jobId, type), needDependencies);
            if (timeFlag > 0) {
                timeScheduler.addJob(jobId);
            }

            response = ServerSubmitJobResponse.newBuilder().setSuccess(true).setJobId(jobId).build();
            getSender().tell(response, getSelf());

        } catch (Exception e) {
            response = ServerSubmitJobResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
        }
    }

    /**
     * 修改任务
     *
     * @param msg
     * @throws IOException
     */
    @Transactional
    private void modifyJob(RestModifyJobRequest msg) throws Exception {

        ServerModifyJobResponse response;
        try {
            // 参数检查
            Job job = convertValidService.convert2Job(msg);

            // 1. update job to DB
            jobService.updateJob(job);

            modifyJobFlag(msg);

            modifyDependency(msg);

            modifyJobExpression(msg);

            // 3. scheduler modify job
            // timeScheduler.modifyJob(jobId);

            response = ServerModifyJobResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            response = ServerModifyJobResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
        }
    }

    private void modifyJobFlag(RestModifyJobRequest msg) throws JobScheduleException {
        if (!msg.hasJobFlag())
            return;
        long jobId = msg.getJobId();
        JobStatus flag = JobStatus.getInstance(msg.getJobFlag());
        timeScheduler.modifyJobFlag(jobId, flag);
        dagScheduler.getJobGraph().modifyJobFlag(jobId, flag);
    }

    private void modifyJobExpression(RestModifyJobRequest msg) {
        if (!msg.hasExpressionEntry()) {
            return;
        }
        long jobId = msg.getJobId();
        ScheduleExpressionEntry expressionEntry = msg.getExpressionEntry();
        String newExpression = expressionEntry.getScheduleExpression();
        // newExpression为空表示删除调度表达式
        if (newExpression == null || newExpression.isEmpty()) {
            jobService.deleteScheduleExpression(jobId);
        } else {
            JobScheduleExpression record = jobService.getScheduleExpressionByJobId(jobId);
            if (record == null) {
                // 插入新的expression表
                jobService.insertScheduleExpression(jobId, expressionEntry);
            } else {
                // 更新旧的expression表
                jobService.updateScheduleExpression(jobId, expressionEntry);
            }
        }

        int dependFlag = dagScheduler.getJobGraph().getParents(jobId).isEmpty() ? 0 : 1;
        int timeFlag = 0;
        int cycleFlag = 0;
        if (newExpression != null && !newExpression.isEmpty()) {
            if (expressionEntry.getExpressionType() == ScheduleExpressionType.FIXED_DELAY.getValue()) {
                cycleFlag = 1;
            } else {
                timeFlag = 1;
            }
        }
        DAGJobType type = DAGJobType.getDAGJobType(cycleFlag, dependFlag, timeFlag);
        dagScheduler.getJobGraph().modifyDAGJobType(jobId, type);
        if (timeFlag == 0) {
            timeScheduler.removeJob(jobId);
        }
    }

    @Transactional
    private void modifyDependency(RestModifyJobRequest msg) throws Exception {
        if (msg.getDependencyEntryList() == null || msg.getDependencyEntryList().isEmpty()) {
            return;
        }
        long jobId = msg.getJobId();
        List<ModifyDependEntry> dependEntries = new ArrayList<>();
        for (DependencyEntry entry : msg.getDependencyEntryList()) {
            long preJobId = entry.getJobId();
            int commonStrategyValue = entry.getCommonDependStrategy();
            String offsetStrategyValue = entry.getOffsetDependStrategy();
            String user = msg.getUser();

            ModifyOperation operation;
            OperationMode operationMode = OperationMode.getInstance(entry.getOperator());
            if (operationMode.equals(OperationMode.ADD)) {
                operation = ModifyOperation.ADD;
                JobDepend jobDepend = convertValidService.convert2JobDepend(jobId, entry, user);
                jobService.insertJobDepend(jobDepend);
            } else if (operationMode.equals(OperationMode.DELETE)) {
                operation = ModifyOperation.DEL;
                jobService.deleteJobDepend(jobId, preJobId);
            } else {
                operation = ModifyOperation.MODIFY;
                JobDependKey key = new JobDependKey();
                key.setJobId(jobId);
                key.setPreJobId(preJobId);
                JobDepend record = jobService.getJobDepend(key);
                if (record != null) {
                    record.setCommonStrategy(commonStrategyValue);
                    record.setOffsetStrategy(offsetStrategyValue);
                    record.setUpdateUser(user);
                    record.setUpdateTime(DateTime.now().toDate());
                    jobService.updateJobDepend(record);
                }
            }
            ModifyDependEntry dependEntry = new ModifyDependEntry(operation, preJobId, commonStrategyValue, offsetStrategyValue);
            dependEntries.add(dependEntry);
        }

        dagScheduler.getJobGraph().modifyDependency(jobId, dependEntries);
    }

    /**
     * 查询任务关系
     *
     * @param msg
     * @throws IOException
     */
    private void queryJobRelation(RestQueryJobRelationRequest msg) throws Exception {

        ServerQueryJobRelationResponse response;
        try {
            long jobId = msg.getJobId();
            ServerQueryJobRelationResponse.Builder builder = ServerQueryJobRelationResponse.newBuilder();
            List<Pair<Long, JobStatus>> relations;
            if (msg.getRelationType() == (JobRelationType.PARENT.getValue())) {
                relations = dagScheduler.getJobGraph().getParents(jobId);
            } else {
                relations = dagScheduler.getJobGraph().getChildren(jobId);
            }
            for (Pair<Long, JobStatus> relation : relations) {
                long relationId = relation.getFirst();
                JobStatus flag = relation.getSecond();
                if (flag.equals(JobStatus.ENABLE) && !jobService.isActive(relationId)) {
                    flag = JobStatus.EXPIRED;
                }
                JobFlagEntry entry = JobFlagEntry.newBuilder().setJobId(relationId).setJobFlag(flag.getValue()).build();
                builder.addJobFlagEntry(entry);
            }
            response = builder.setSuccess(true).build();
            getSender().tell(response, getSelf());

        } catch (Exception e) {
            response = ServerQueryJobRelationResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
            throw e;
        }

    }

    /**
     * 测试用
     *
     * @param msg
     * @throws IOException
     */
    @Transactional
    private void removeJob(RemoveJobRequest msg) throws IOException {
        long jobId = msg.getJobId();
        try {
            // remove job
            jobService.deleteJob(jobId);
            // remove job depend where preJobId=jobId
            jobService.deleteJobDependByPreJob(jobId);
            // remove expression where jobId=jobId
            jobService.deleteScheduleExpression(jobId);
            // scheduler remove job
            timeScheduler.removeJob(jobId);
            dagScheduler.getJobGraph().removeJob(jobId);
            getSender().tell("remove success", getSelf());
        } catch (Exception e) {
            getSender().tell("remove failed", getSelf());
            throw new IOException(e);
        }
    }
}
