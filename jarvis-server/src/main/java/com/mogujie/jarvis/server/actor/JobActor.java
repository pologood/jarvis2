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
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.mybatis.guice.transactional.Transactional;

import akka.actor.Props;
import akka.actor.UntypedActor;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.domain.JobRelationType;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.expression.CronExpression;
import com.mogujie.jarvis.core.expression.FixedDelayExpression;
import com.mogujie.jarvis.core.expression.FixedRateExpression;
import com.mogujie.jarvis.core.expression.ISO8601Expression;
import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.JobDependKey;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.JobProtos.JobStatusEntry;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobStatusRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestQueryJobRelationRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobStatusResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerQueryJobRelationResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.domain.JobEntry;
import com.mogujie.jarvis.server.domain.ModifyDependEntry;
import com.mogujie.jarvis.server.domain.RemoveJobRequest;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.dag.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;
import com.mogujie.jarvis.server.scheduler.dag.JobGraph;
import com.mogujie.jarvis.server.scheduler.time.TimePlan;
import com.mogujie.jarvis.server.service.ConvertValidService;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.PlanUtil;

/**
 * @author guangming
 */
public class JobActor extends UntypedActor {

    private JobGraph jobGraph = JobGraph.INSTANCE;
    private TimePlan plan = TimePlan.INSTANCE;

    private JobService jobService = Injectors.getInjector().getInstance(JobService.class);
    private ConvertValidService convertValidService = Injectors.getInjector().getInstance(ConvertValidService.class);

    public static Props props() {
        return Props.create(JobActor.class);
    }

    /**
     * 处理消息
     *
     * @return
     */
    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestSubmitJobRequest.class, ServerSubmitJobResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestModifyJobRequest.class, ServerModifyJobResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestModifyJobStatusRequest.class, ServerModifyJobStatusResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestQueryJobRelationRequest.class, ServerQueryJobRelationResponse.class, MessageType.GENERAL));
        return list;
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestSubmitJobRequest) {
            submitJob((RestSubmitJobRequest) obj);
        } else if (obj instanceof RestModifyJobRequest) {
            modifyJob((RestModifyJobRequest) obj);
        } else if (obj instanceof RestModifyJobStatusRequest) {
            modifyJobStatus((RestModifyJobStatusRequest) obj);
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
            Job job = convertValidService.convertCheck2Job(msg);

            // 1. insert job to DB
            long jobId = jobService.insertJob(job);

            // 2. insert schedule expression to DB
            List<ScheduleExpressionEntry> expressionEntries = msg.getExpressionEntryList();
            if (expressionEntries != null && !expressionEntries.isEmpty()) {
                for (ScheduleExpressionEntry entry : expressionEntries) {
                    jobService.insertScheduleExpression(jobId, entry);
                }
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
            Map<Long, ScheduleExpression> timeExpressions = jobService.get(jobId).getScheduleExpressions();
            if (!timeExpressions.isEmpty()) {
                for (ScheduleExpression expression : timeExpressions.values()) {
                    if (expression instanceof CronExpression || expression instanceof FixedRateExpression
                            || expression instanceof ISO8601Expression) {
                        timeFlag = 1;
                    } else if (expression instanceof FixedDelayExpression) {
                        cycleFlag = 1;
                    }
                }
            }
            int dependFlag = (!needDependencies.isEmpty()) ? 1 : 0;
            DAGJobType type = DAGJobType.getDAGJobType(cycleFlag, dependFlag, timeFlag);
            jobGraph.addJob(jobId, new DAGJob(jobId, type), needDependencies);
            if (type.equals(DAGJobType.TIME)) {
                plan.addJob(jobId);
            }

            response = ServerSubmitJobResponse.newBuilder().setSuccess(true).setJobId(jobId).build();
            getSender().tell(response, getSelf());

        } catch (Exception e) {
            // TODO rollback
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
            Job job = convertValidService.convertCheck2Job(msg);

            // 1. update job to DB
            jobService.updateJob(job);

            modifyDependency(msg);

            modifyJobExpression(msg);

            long jobId = msg.getJobId();
            int dependFlag = jobGraph.getParents(jobId).isEmpty() ? 0 : 1;
            JobEntry jobEntry = jobService.get(jobId);
            int cycleFlag = 0;
            int timeFlag = 0;
            Map<Long, ScheduleExpression> timeExpressions = jobEntry.getScheduleExpressions();
            if (!timeExpressions.isEmpty()) {
                for (ScheduleExpression expression : timeExpressions.values()) {
                    if (expression instanceof CronExpression || expression instanceof FixedRateExpression
                            || expression instanceof ISO8601Expression) {
                        timeFlag = 1;
                    } else if (expression instanceof FixedDelayExpression) {
                        cycleFlag = 1;
                    }
                }
            }
            DAGJobType type = DAGJobType.getDAGJobType(cycleFlag, dependFlag, timeFlag);
            jobGraph.modifyDAGJobType(jobId, type);

            List<ScheduleExpressionEntry> expressionEntries = msg.getExpressionEntryList();
            //如果修改过时间，可能要修改时间计划
            if (expressionEntries != null && !expressionEntries.isEmpty()) {
                // 如果是纯时间任务
                if (type.equals(DAGJobType.TIME)) {
                    //重新计算下一次时间
                    DateTime now = DateTime.now();
                    DateTime lastTime = PlanUtil.getScheduleTimeBefore(jobId, now);
                    if (lastTime == null) {
                        lastTime = new DateTime(0);
                    } else {
                        lastTime = lastTime.minusSeconds(1);
                    }
                    DateTime nextTime = PlanUtil.getScheduleTimeAfter(jobId, now);
                    TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);
                    List<Task> tasks = taskService.getTasksBetween(jobId, Range.closed(lastTime, nextTime));
                    if (tasks != null && !tasks.isEmpty()) {
                        //如果当前周期已经跑过一次，则下一周期生效
                        // noting to do
                    } else {
                        //如果当前周期尚未开始跑，则立即生效，重新计算下一次时间
                        plan.removeJob(jobId);
                        plan.addJob(jobId);
                    }
                } else if (!type.implies(DAGJobType.TIME)) {
                    plan.removeJob(jobId);
                }
            }

            response = ServerModifyJobResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            response = ServerModifyJobResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
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
    private void modifyJobStatus(RestModifyJobStatusRequest msg) throws Exception {

        ServerModifyJobStatusResponse response;
        try {
            long jobId = msg.getJobId();
            // 参数检查
            Job job = convertValidService.convertCheck2Job(msg);

            // 1. update job to DB
            JobStatus oldStatus = JobStatus.parseValue(jobService.get(jobId).getJob().getStatus());
            jobService.updateJob(job);

            JobStatus newStatus = JobStatus.parseValue(msg.getStatus());
            plan.modifyJobFlag(jobId, oldStatus, newStatus);
            jobGraph.modifyJobFlag(jobId, oldStatus, newStatus);

            response = ServerModifyJobStatusResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            response = ServerModifyJobStatusResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
        }
    }

    private void modifyJobExpression(RestModifyJobRequest msg) {
        List<ScheduleExpressionEntry> expressionEntries = msg.getExpressionEntryList();
        if (expressionEntries == null || expressionEntries.isEmpty()) {
            return;
        }
        long jobId = msg.getJobId();
        for (ScheduleExpressionEntry entry : expressionEntries) {
            OperationMode operation = OperationMode.parseValue(entry.getOperator());
            if (operation.equals(OperationMode.ADD)) {
                jobService.insertScheduleExpression(jobId, entry);
            } else if (operation.equals(OperationMode.DELETE)) {
                jobService.deleteScheduleExpression(jobId, entry.getExpressionId());
            } else if (operation.equals(OperationMode.EDIT)) {
                jobService.updateScheduleExpression(jobId, entry);
            }
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

            OperationMode operationMode = OperationMode.parseValue(entry.getOperator());
            if (operationMode.equals(OperationMode.ADD)) {
                JobDepend jobDepend = convertValidService.convert2JobDepend(jobId, entry, user);
                jobService.insertJobDepend(jobDepend);
            } else if (operationMode.equals(OperationMode.DELETE)) {
                jobService.deleteJobDepend(jobId, preJobId);
            } else {
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
            ModifyDependEntry dependEntry = new ModifyDependEntry(operationMode, preJobId, commonStrategyValue, offsetStrategyValue);
            dependEntries.add(dependEntry);
        }

        jobGraph.modifyDependency(jobId, dependEntries);
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
                relations = jobGraph.getParents(jobId);
            } else {
                relations = jobGraph.getChildren(jobId);
            }
            for (Pair<Long, JobStatus> relation : relations) {
                long relationId = relation.getFirst();
                JobStatus flag = relation.getSecond();
                if (flag.equals(JobStatus.ENABLE) && !jobService.isActive(relationId)) {
                    flag = JobStatus.EXPIRED;
                }
                JobStatusEntry entry = JobStatusEntry.newBuilder().setJobId(relationId).setStatus(flag.getValue()).build();
                builder.addJobStatusEntry(entry);
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
            jobService.deleteScheduleExpressionByJobId(jobId);
            // scheduler remove job
            plan.removeJob(jobId);
            jobGraph.removeJob(jobId);
            getSender().tell("remove success", getSelf());
        } catch (Exception e) {
            getSender().tell("remove failed", getSelf());
            throw new IOException(e);
        }
    }
}
