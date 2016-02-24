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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.mybatis.guice.transactional.Transactional;

import akka.actor.Props;
import akka.actor.UntypedActor;

import com.google.common.base.Throwables;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.AppType;
import com.mogujie.jarvis.core.domain.JobRelationType;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.exception.NotFoundException;
import com.mogujie.jarvis.core.expression.CronExpression;
import com.mogujie.jarvis.core.expression.FixedDelayExpression;
import com.mogujie.jarvis.core.expression.FixedRateExpression;
import com.mogujie.jarvis.core.expression.ISO8601Expression;
import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.dto.generate.App;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.JobDependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.JobProtos.JobStatusEntry;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobDependRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobScheduleExpRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobStatusRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestQueryJobRelationRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestRemoveJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobDependResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobScheduleExpResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobStatusResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerQueryJobRelationResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerRemoveJobResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.protocol.JobScheduleExpressionEntryProtos.ScheduleExpressionEntry;
import com.mogujie.jarvis.protocol.SearchJobProtos.RestSearchAllJobsRequest;
import com.mogujie.jarvis.protocol.SearchJobProtos.RestSearchBizIdByNameRequest;
import com.mogujie.jarvis.protocol.SearchJobProtos.RestSearchJobByNameRequest;
import com.mogujie.jarvis.protocol.SearchJobProtos.RestSearchJobByScriptIdRequest;
import com.mogujie.jarvis.protocol.SearchJobProtos.RestSearchPreJobInfoRequest;
import com.mogujie.jarvis.protocol.SearchJobProtos.RestSearchScriptTypeRequest;
import com.mogujie.jarvis.protocol.SearchJobProtos.ServerSearchAllJobsResponse;
import com.mogujie.jarvis.protocol.SearchJobProtos.ServerSearchBizIdByNamResponse;
import com.mogujie.jarvis.protocol.SearchJobProtos.ServerSearchJobByNameResponse;
import com.mogujie.jarvis.protocol.SearchJobProtos.ServerSearchJobByScriptIdResponse;
import com.mogujie.jarvis.protocol.SearchJobProtos.ServerSearchPreJobInfoResponse;
import com.mogujie.jarvis.protocol.SearchJobProtos.ServerSearchScriptTypeResponse;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.domain.ModifyDependEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.dag.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;
import com.mogujie.jarvis.server.scheduler.dag.JobGraph;
import com.mogujie.jarvis.server.scheduler.time.TimePlan;
import com.mogujie.jarvis.server.service.AppService;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.PlanService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.service.ValidService;
import com.mogujie.jarvis.server.service.ValidService.CheckMode;
import com.mogujie.jarvis.server.util.PlanUtil;

/**
 * @author guangming
 */
public class JobActor extends UntypedActor {

    private static Logger LOGGER = LogManager.getLogger();

    private JobGraph jobGraph = JobGraph.INSTANCE;
    private TimePlan plan = TimePlan.INSTANCE;

    private JobService jobService = Injectors.getInjector().getInstance(JobService.class);
    private AppService appService = Injectors.getInjector().getInstance(AppService.class);
    private ValidService validService = Injectors.getInjector().getInstance(ValidService.class);

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
        list.add(new ActorEntry(RestModifyJobDependRequest.class, ServerModifyJobDependResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestModifyJobScheduleExpRequest.class, ServerModifyJobScheduleExpResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestModifyJobStatusRequest.class, ServerModifyJobStatusResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestQueryJobRelationRequest.class, ServerQueryJobRelationResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestRemoveJobRequest.class, ServerRemoveJobResponse.class, MessageType.GENERAL));
        // ----- 兼容老系统接口---
        list.add(new ActorEntry(RestSearchJobByScriptIdRequest.class, ServerSearchJobByScriptIdResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestSearchJobByNameRequest.class, ServerSearchJobByNameResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestSearchPreJobInfoRequest.class, ServerSearchPreJobInfoResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestSearchAllJobsRequest.class, ServerSearchAllJobsResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestSearchScriptTypeRequest.class, ServerSearchScriptTypeResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestSearchBizIdByNameRequest.class, ServerSearchBizIdByNamResponse.class, MessageType.GENERAL));
        //---------
        return list;
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        LOGGER.info("receive {}", obj.getClass().getSimpleName());
        try {
            if (obj instanceof RestSubmitJobRequest) {
                submitJob((RestSubmitJobRequest) obj);
                updatePlan();
            } else if (obj instanceof RestModifyJobRequest) {
                modifyJob((RestModifyJobRequest) obj);
                updatePlan();
            } else if (obj instanceof RestModifyJobDependRequest) {
                modifyJobDependency((RestModifyJobDependRequest) obj);
                updatePlan();
            } else if (obj instanceof RestModifyJobScheduleExpRequest) {
                modifyJobScheduleExp((RestModifyJobScheduleExpRequest) obj);
                updatePlan();
            } else if (obj instanceof RestModifyJobStatusRequest) {
                modifyJobStatus((RestModifyJobStatusRequest) obj);
                updatePlan();
            } else if (obj instanceof RestQueryJobRelationRequest) {
                RestQueryJobRelationRequest msg = (RestQueryJobRelationRequest) obj;
                queryJobRelation(msg);
            } else if (obj instanceof RestRemoveJobRequest) { // 回滚和测试用，无须加入handleMessage
                RestRemoveJobRequest msg = (RestRemoveJobRequest) obj;
                removeJob(msg.getJobId());
                ServerRemoveJobResponse response = ServerRemoveJobResponse.newBuilder().setSuccess(true).build();
                getSender().tell(response, getSelf());
            } else if (obj instanceof RestSearchJobByScriptIdRequest) {
                RestSearchJobByScriptIdRequest msg = (RestSearchJobByScriptIdRequest) obj;
                searchJobByScriptId(msg);
            } else if (obj instanceof RestSearchJobByNameRequest) {
                RestSearchJobByNameRequest msg = (RestSearchJobByNameRequest) obj;
                searchJobByName(msg);
            } else if (obj instanceof RestSearchPreJobInfoRequest) {
                RestSearchPreJobInfoRequest msg = (RestSearchPreJobInfoRequest) obj;
                searchPreJobInfo(msg);
            } else if (obj instanceof RestSearchAllJobsRequest) {
                RestSearchAllJobsRequest msg = (RestSearchAllJobsRequest) obj;
                searchAllJobs(msg);
            } else if (obj instanceof RestSearchScriptTypeRequest) {
                RestSearchScriptTypeRequest msg = (RestSearchScriptTypeRequest) obj;
                searchScriptType(msg);
            } else if (obj instanceof RestSearchBizIdByNameRequest) {
                RestSearchBizIdByNameRequest msg = (RestSearchBizIdByNameRequest) obj;
                searchBizIdByName(msg);
            } else {
                unhandled(obj);
            }
        } catch (Exception e) {
            throw e;
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
        long jobId = 0;
        try {
            DateTime now = DateTime.now();

            // 参数检查
            Job job = msg2Job(msg);
            validService.checkJob(CheckMode.ADD, job);

            // 1. insert job to DB
            jobId = jobService.insertJob(job);
            job = jobService.get(jobId).getJob();

            // 2. insert schedule expression to DB
            List<ScheduleExpressionEntry> expressionEntries = msg.getExpressionEntryList();
            if (expressionEntries != null && !expressionEntries.isEmpty()) {
                for (ScheduleExpressionEntry entry : expressionEntries) {
                    jobService.insertScheduleExpression(jobId, entry);
                }
            }

            // 3. insert jobDepend to DB
            Set<Long> dependencies = Sets.newHashSet();
            if (msg.getDependencyEntryList() != null) {
                for (DependencyEntry entry : msg.getDependencyEntryList()) {
                    dependencies.add(entry.getJobId());
                    JobDepend jobDepend = convert2JobDepend(jobId, entry, msg.getUser(), now);
                    jobService.insertJobDepend(jobDepend);
                }
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
            //过滤DELETED父任务
            Set<Long> needDependencies = Sets.newHashSet();
            for (long preJobId : dependencies) {
                if (jobService.get(preJobId).getJob().getStatus() != JobStatus.DELETED.getValue()) {
                    needDependencies.add(preJobId);
                }
            }
            int dependFlag = (!needDependencies.isEmpty()) ? 1 : 0;
            DAGJobType type = DAGJobType.getDAGJobType(timeFlag, dependFlag, cycleFlag);
            jobGraph.addJob(jobId, new DAGJob(jobId, type), needDependencies);
            if (type.equals(DAGJobType.TIME) || job.getIsTemp()) {
                plan.addJob(jobId);
            }

            response = ServerSubmitJobResponse.newBuilder().setSuccess(true).setJobId(jobId).build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            // roll back submit job
            removeJob(jobId);
            response = ServerSubmitJobResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
            LOGGER.error("", e);
            Throwables.propagate(e);
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
            Job job = msg2Job(msg);
            validService.checkJob(CheckMode.EDIT, job);

            // update job to DB
            jobService.updateJob(job);

            response = ServerModifyJobResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            response = ServerModifyJobResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
            LOGGER.error("", e);
            throw e;
        }
    }

    /**
     * 修改任务依赖
     *
     * @param msg
     * @throws IOException
     */
    @Transactional
    private void modifyJobDependency(RestModifyJobDependRequest msg) throws Exception {
        ServerModifyJobDependResponse response;
        long jobId = msg.getJobId();
        DAGJob dagJob = jobGraph.getDAGJob(jobId);
        if (dagJob == null) {
            response = ServerModifyJobDependResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(jobId + "is not existed.")
                    .build();
            getSender().tell(response, getSelf());
            return;
        }
        List<DAGJob> oldParents = jobGraph.getParents(dagJob);

        try {
            // 参数检查
            validService.checkJobDependency(msg);

            // 1. update jobService
            List<ModifyDependEntry> dependEntries = new ArrayList<>();
            DateTime now = DateTime.now();
            for (DependencyEntry entry : msg.getDependencyEntryList()) {
                JobDepend jobDepend = convert2JobDepend(jobId, entry, msg.getUser(), now);
                OperationMode operationMode = OperationMode.parseValue(entry.getOperator());

                if (operationMode == OperationMode.ADD) {
                    jobService.insertJobDepend(jobDepend);
                } else if (operationMode == OperationMode.DELETE) {
                    jobService.deleteJobDepend(jobId, entry.getJobId());
                } else {
                    jobService.updateJobDepend(jobDepend);
                }

                ModifyDependEntry dependEntry = new ModifyDependEntry(operationMode, entry.getJobId(), entry.getCommonDependStrategy(),
                        entry.getOffsetDependStrategy());
                dependEntries.add(dependEntry);
            }

            // 2. update jobGraph
            jobGraph.modifyDependency(jobId, dependEntries);

            response = ServerModifyJobDependResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            // roll back modify dependency
            jobGraph.setParents(dagJob, oldParents);
            response = ServerModifyJobDependResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
            LOGGER.error("", e);
            throw e;
        }
    }

    /**
     * 修改任务计划表达式
     *
     * @param msg
     * @throws IOException
     */
    @Transactional
    private void modifyJobScheduleExp(RestModifyJobScheduleExpRequest msg) throws Exception {
        long jobId = msg.getJobId();
        ServerModifyJobScheduleExpResponse response;

        try {
            // 参数检查
            validService.check2JobScheduleExp(msg);

            // 1. update jobService
            List<ScheduleExpressionEntry> expressionEntries = msg.getExpressionEntryList();
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

            // 2. update jobGraph
            boolean timeFlag = false;
            boolean cycleFlag = false;
            Map<Long, ScheduleExpression> timeExpressions = jobService.get(jobId).getScheduleExpressions();
            if (!timeExpressions.isEmpty()) {
                for (ScheduleExpression expression : timeExpressions.values()) {
                    if (expression instanceof CronExpression || expression instanceof FixedRateExpression
                            || expression instanceof ISO8601Expression) {
                        timeFlag = true;
                    } else if (expression instanceof FixedDelayExpression) {
                        cycleFlag = true;
                    }
                }
            }
            DAGJob dagJob = jobGraph.getDAGJob(jobId);
            if (dagJob != null) {
                dagJob.updateJobTypeByTimeFlag(timeFlag);
                dagJob.updateJobTypeByCycleFlag(cycleFlag);
            }

            // 3. update next plan
            DAGJobType type = jobGraph.getDAGJob(jobId).getType();
            // 如果是纯时间任务
            if (type.equals(DAGJobType.TIME)) {
                //重新计算下一次时间
                DateTime now = DateTime.now();
                DateTime lastTime = PlanUtil.getScheduleTimeBefore(jobId, now);
                if (lastTime == null) {
                    lastTime = JarvisConstants.DATETIME_MIN;
                }
                DateTime nextTime = PlanUtil.getScheduleTimeAfter(jobId, now);
                if (nextTime == null) {
                    nextTime = JarvisConstants.DATETIME_MAX;
                }
                TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);
                List<Task> tasks = taskService.getTasksBetween(jobId, Range.closed(lastTime.minusSeconds(1), nextTime));
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

            response = ServerModifyJobScheduleExpResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            response = ServerModifyJobScheduleExpResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
            LOGGER.error("", e);
            throw e;
        }
    }

    /**
     * 修改任务状态
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
            Job job = msg2Job(msg);
            validService.checkJob(CheckMode.EDIT_STATUS, job);

            // 1. update job to DB
            JobStatus oldStatus = JobStatus.parseValue(jobService.get(jobId).getJob().getStatus());
            if (oldStatus.equals(JobStatus.DELETED)) {
                response = ServerModifyJobStatusResponse.newBuilder().setSuccess(false)
                        .setMessage("已进入垃圾箱的job不允许修改状态")
                        .build();
            } else {
                jobService.updateJob(job);
                JobStatus newStatus = JobStatus.parseValue(msg.getStatus());
                plan.modifyJobFlag(jobId, oldStatus, newStatus);
                jobGraph.modifyJobFlag(jobId, oldStatus, newStatus);
                response = ServerModifyJobStatusResponse.newBuilder().setSuccess(true).build();
            }
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            response = ServerModifyJobStatusResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
            LOGGER.error("", e);
            throw e;
        }
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
            LOGGER.info("start queryJobRelation");
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
            LOGGER.error("", e);
            throw e;
        }
    }

    private Job msg2Job(RestSubmitJobRequest msg) throws NotFoundException {
        Job job = new Job();
        job.setAppId(analysisAppId(msg.getAppAuth(), msg.getAppName()));
        job.setJobName(msg.getJobName());
        job.setContentType(msg.getContentType());
        job.setContent(msg.getContent());
        job.setParams(msg.getParameters());
        job.setPriority(msg.getPriority());
        job.setStatus(msg.getStatus());
        job.setJobType(msg.getJobType());
        job.setWorkerGroupId(msg.getWorkerGroupId());
        job.setBizGroupId(msg.getBizGroupId());
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
        if (msg.hasIsSerial()) {
            job.setIsSerial(msg.getIsSerial());
        }
        if (msg.hasIsTemp()) {
            job.setIsTemp(msg.getIsTemp());
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

    private Job msg2Job(RestModifyJobRequest msg) throws NotFoundException {
        Job job = new Job();
        job.setJobId(msg.getJobId());
        job.setAppId(analysisAppId(msg.getAppAuth(), msg.hasAppName() ? msg.getAppName() : null));
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
        if (msg.hasBizGroupId()) {
            job.setBizGroupId(msg.getBizGroupId());
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

    public Job msg2Job(RestModifyJobStatusRequest msg) {
        Job job = new Job();
        job.setJobId(msg.getJobId());
        job.setStatus(msg.getStatus());
        return job;
    }

    /**
     * 转化为_jobDepend
     */
    private JobDepend convert2JobDepend(Long jobId, DependencyEntry entry, String user, DateTime time) {
        JobDepend jobDepend = new JobDepend();
        jobDepend.setJobId(jobId);
        jobDepend.setPreJobId(entry.getJobId());
        jobDepend.setCommonStrategy(entry.getCommonDependStrategy());
        jobDepend.setOffsetStrategy(entry.getOffsetDependStrategy());
        jobDepend.setUpdateTime(time.toDate());
        jobDepend.setUpdateUser(user);

        if (entry.getOperator() == OperationMode.ADD.getValue()) {
            jobDepend.setCreateTime(time.toDate());
        }
        return jobDepend;
    }

    /**
     * 分析获得appId
     */
    private int analysisAppId(AppAuthProtos.AppAuth appAuth, String appName) throws NotFoundException {

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

    /**
     * 测试用
     *
     * @param jobId
     * @throws IOException
     */
    @Transactional
    private void removeJob(long jobId) throws IOException {
        // remove job
        jobService.deleteJobAndRelation(jobId);
        // scheduler remove job
        plan.removeJob(jobId);
        jobGraph.removeJob(jobId);
    }

    private void updatePlan() {
        PlanService planService = Injectors.getInjector().getInstance(PlanService.class);
        DateTime now = DateTime.now();
        Range<DateTime> range = Range.closedOpen(now.withTimeAtStartOfDay(), now.plusDays(1).withTimeAtStartOfDay());
        planService.updateJobIds(range);
    }

    private void searchJobByScriptId(RestSearchJobByScriptIdRequest msg) throws Exception {
        //TODO
    }

    private void searchJobByName(RestSearchJobByNameRequest msg) throws Exception {
        //TODO
    }

    private void searchPreJobInfo(RestSearchPreJobInfoRequest msg) throws Exception {
        //TODO
    }

    private void searchAllJobs(RestSearchAllJobsRequest msg) throws Exception {
        //TODO
    }

    private void searchScriptType(RestSearchScriptTypeRequest msg) throws Exception {
        //TODO
    }

    private void searchBizIdByName(RestSearchBizIdByNameRequest msg) throws Exception {
        //TODO
    }
}
