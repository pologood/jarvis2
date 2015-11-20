/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月15日 下午2:35:11
 */
package com.mogujie.jarvis.server.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.expression.CronExpression;
import com.mogujie.jarvis.core.expression.DefaultDependencyStrategyExpression;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.DependencyStrategyExpression;
import com.mogujie.jarvis.core.expression.FixedDelayExpression;
import com.mogujie.jarvis.core.expression.FixedRateExpression;
import com.mogujie.jarvis.core.expression.ISO8601Expression;
import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.core.expression.ScheduleExpressionType;
import com.mogujie.jarvis.core.expression.TimeOffsetExpression;
import com.mogujie.jarvis.dao.AppMapper;
import com.mogujie.jarvis.dao.JobDependMapper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dao.JobScheduleExpressionMapper;
import com.mogujie.jarvis.dto.App;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.dto.JobDependExample;
import com.mogujie.jarvis.dto.JobExample;
import com.mogujie.jarvis.dto.JobScheduleExpression;
import com.mogujie.jarvis.dto.JobScheduleExpressionExample;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.domain.JobEntry;
import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;

/**
 * @author wuya
 *
 */
@Service
public class JobService {

    private static final Logger LOGGER = LogManager.getLogger();
    private Map<Long, JobEntry> metaStore = Maps.newConcurrentMap();

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private JobScheduleExpressionMapper jobScheduleExpressionMapper;

    @Autowired
    private JobDependMapper jobDependMapper;

    @Autowired
    private AppMapper appMapper;

    @PostConstruct
    private void init(){
        loadMetaDataFromDB();
        LOGGER.info("jobService loadMetaDataFromDB finished.");
    }

    public JobEntry get(long jobId) {
        return metaStore.get(jobId);
    }

    public Map<Long, JobEntry> getMetaStore(){
        return metaStore;
    }

//    public void addJob(Job job, List<ScheduleExpression> scheduleExpressions, Map<Long, JobDependencyEntry> dependencies) {
//        metaStore.put(job.getJobId(), new JobEntry(job, scheduleExpressions, dependencies));
//    }
//
//    public void remove(long jobId) {
//        metaStore.remove(jobId);
//    }

    /**
     * 获取活跃的job（状态为enable，并且不是过期的）
     * @return
     */
    public List<Job> getActiveJobs() {
        JobExample example = new JobExample();
        example.createCriteria().andJobFlagEqualTo(JobFlag.ENABLE.getValue());
        List<Job> jobs = jobMapper.selectByExample(example);
        if(jobs == null || jobs.isEmpty()){
            return jobs;
        }
        //移除 不在有效期的job
        Date  now = DateTime.now().toDate();
        Iterator<Job> iterator = jobs.iterator();
        while(iterator.hasNext()){
            Job job = iterator.next();
            if((job.getActiveEndDate() != null && job.getActiveEndDate().getTime() < now.getTime())
               || (job.getActiveStartDate() != null && job.getActiveStartDate().getTime() > now.getTime())    ){
                iterator.remove();
            }
        }
        return jobs;
    }



    public List<Job> getNotDeletedJobs() {
        JobExample example = new JobExample();
        example.createCriteria().andJobFlagNotEqualTo(JobFlag.DELETED.getValue());
        return jobMapper.selectByExample(example);
    }

    public List<Job> getActiveExpiredJobs() {
        JobExample example = new JobExample();
        DateTime dt = DateTime.now();
        List<Integer> activeJobFlags = Lists.newArrayList(JobFlag.ENABLE.getValue(),
                JobFlag.DISABLE.getValue());
        example.createCriteria().andActiveEndDateLessThan(dt.toDate()).andJobFlagIn(activeJobFlags);
        return jobMapper.selectByExample(example);
    }

    public void updateJobFlag(long jobId, String user, int newFlag) {
        Job record = jobMapper.selectByPrimaryKey(jobId);
        record.setJobFlag(newFlag);
        record.setUpdateUser(user);
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        record.setUpdateTime(currentTime);
        jobMapper.updateByPrimaryKey(record);
    }

    public DAGJobType getDAGJobType(long jobId) {
        JobEntry jobEntry = get(jobId);
        Set<Long> dependencies = jobEntry.getDependencies().keySet();
        int cycleFlag = 0;
        int timeFlag = 0;
        List<ScheduleExpression> timeExpressions = jobEntry.getScheduleExpressions();
        if (!timeExpressions.isEmpty()) {
            for (ScheduleExpression expression : timeExpressions) {
                if (expression instanceof CronExpression || expression instanceof FixedRateExpression
                        || expression instanceof ISO8601Expression) {
                    timeFlag = 1;
                } else if (expression instanceof FixedDelayExpression) {
                    cycleFlag = 1;
                }
            }
        }
        int dependFlag = (!dependencies.isEmpty()) ? 1 : 0;

        return DAGJobType.getDAGJobType(timeFlag, dependFlag, cycleFlag);
    }

    public String getAppName(long jobId) {
        App app = appMapper.selectByPrimaryKey(get(jobId).getJob().getAppId());
        return app.getAppName();
    }

    public DateTime getScheduleTimeAfter(long jobId, DateTime dateTime) {
        DateTime scheduleTime = null;
        List<ScheduleExpression> expressions = get(jobId).getScheduleExpressions();
        if (expressions != null && expressions.size() > 0) {
            for (ScheduleExpression scheduleExpression : expressions) {
                DateTime nextTime = scheduleExpression.getTimeAfter(dateTime);
                if (scheduleTime == null || scheduleTime.isAfter(nextTime)) {
                    scheduleTime = nextTime;
                }
            }
        }

        return scheduleTime;
    }


    /**
     * 读取metaData
     */
    private void loadMetaDataFromDB(){

        JobExample jobExample = new JobExample();
        jobExample.createCriteria().andJobFlagNotEqualTo(JobFlag.DELETED.getValue());
        List<Job> jobs = jobMapper.selectByExample(jobExample);

        List<JobScheduleExpression> scheduleExpressions = jobScheduleExpressionMapper.selectByExample(new JobScheduleExpressionExample());
        Multimap<Long, ScheduleExpression> scheduleExpressionMap = ArrayListMultimap.create();
        for (JobScheduleExpression jobScheduleExpression : scheduleExpressions) {
            ScheduleExpression scheduleExpression = null;
            int expressionType = jobScheduleExpression.getExpressionType();
            long jobId = jobScheduleExpression.getJobId();

            if (expressionType == ScheduleExpressionType.CRON.getValue()) {
                scheduleExpression = new CronExpression(jobScheduleExpression.getExpression());
            } else if (expressionType == ScheduleExpressionType.FIXED_RATE.getValue()) {
                scheduleExpression = new FixedRateExpression(jobScheduleExpression.getExpression());
            } else if (expressionType == ScheduleExpressionType.FIXED_DELAY.getValue()) {
                scheduleExpression = new FixedDelayExpression(jobScheduleExpression.getExpression());
            } else if (expressionType == ScheduleExpressionType.ISO8601.getValue()) {
                scheduleExpression = new ISO8601Expression(jobScheduleExpression.getExpression());
            }else{
                LOGGER.warn("ExpressionType is undefined. id={};type={}",jobId,expressionType);
                continue;
            }

            if (!scheduleExpression.isValid()) {
                LOGGER.warn("expression value is invalid. id={};value={}",jobId,scheduleExpression.toString());
                continue;
            }

            scheduleExpressionMap.put(jobId, scheduleExpression);
        }

        List<JobDepend> jobDepends = jobDependMapper.selectByExample(new JobDependExample());
        Multimap<Long, JobDepend> jobDependMap = ArrayListMultimap.create();
        for (JobDepend jobDepend : jobDepends) {
            jobDependMap.put(jobDepend.getJobId(), jobDepend);
        }

        for (Job job : jobs) {
            long jobId = job.getJobId();
            List<ScheduleExpression> jobScheduleExpressions = new ArrayList<>(scheduleExpressionMap.get(jobId));
            Map<Long, JobDependencyEntry> dependencies = null;
            Collection<JobDepend> jobDependsCollection = jobDependMap.get(jobId);
            if (jobDependsCollection != null && jobDependsCollection.size() > 0) {
                dependencies = Maps.newHashMap();
                for (JobDepend jobDepend : jobDependsCollection) {
                    String offsetStrategy = jobDepend.getOffsetStrategy();
                    // default is null
                    if (offsetStrategy.isEmpty()) {
                        offsetStrategy = null;
                    }

                    String commonStrategyStr = null;
                    Integer commonStrategy = jobDepend.getCommonStrategy();
                    if (commonStrategy == null) {
                        commonStrategyStr = "*";
                    } else {
                        switch (commonStrategy) {
                            case 1:
                                commonStrategyStr = "L(1)";
                                break;
                            case 2:
                                commonStrategyStr = "+";
                                break;
                            default:
                                commonStrategyStr = "*";
                                break;
                        }
                    }

                    // 检查依赖表达式是否有效
                    DependencyExpression dependencyExpression = new TimeOffsetExpression(offsetStrategy);
                    if (offsetStrategy != null && !dependencyExpression.isValid()) {
                        LOGGER.warn("dependency expression is invalid. id={}; value={}",jobId,dependencyExpression.toString());
                        continue;
                    }

                    // 检查依赖策略表达式是否有效
                    DependencyStrategyExpression dependencyStrategyExpression = new DefaultDependencyStrategyExpression(commonStrategyStr);
                    if (!dependencyStrategyExpression.isValid()) {
                        LOGGER.warn("dependency strategy is invalid. id={}; value={}",jobId,dependencyStrategyExpression.toString());
                        continue;
                    }

                    JobDependencyEntry jobDependencyEntry = new JobDependencyEntry(dependencyExpression, dependencyStrategyExpression);
                    dependencies.put(jobDepend.getPreJobId(), jobDependencyEntry);
                }
            }

            // 初始化 JobMetaStore
            metaStore.put(job.getJobId(), new JobEntry(job,jobScheduleExpressions, dependencies));

//            // 初始化 JobGraph
//            List<Long> dependencyJobIdList = null;
//            if (dependencies != null && dependencies.size() > 0) {
//                dependencyJobIdList = Lists.newArrayList();
//                for (Map.Entry<Long, JobDependencyEntry> entry : dependencies.entrySet()) {
//                    dependencyJobIdList.add(entry.getKey());
//                }
//            }
//
//            jobGraph.addJob(jobId, jobScheduleExpressions, dependencyJobIdList);
        }
    }
}