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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.mybatis.guice.transactional.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.core.domain.JobStatus;
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
import com.mogujie.jarvis.dao.generate.AppMapper;
import com.mogujie.jarvis.dao.generate.JobDependMapper;
import com.mogujie.jarvis.dao.generate.JobMapper;
import com.mogujie.jarvis.dao.generate.JobScheduleExpressionMapper;
import com.mogujie.jarvis.dto.generate.App;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.JobDependExample;
import com.mogujie.jarvis.dto.generate.JobDependKey;
import com.mogujie.jarvis.dto.generate.JobExample;
import com.mogujie.jarvis.dto.generate.JobScheduleExpression;
import com.mogujie.jarvis.dto.generate.JobScheduleExpressionExample;
import com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.domain.JobEntry;

/**
 * @author wuya
 *
 */
@Singleton
public class JobService {

    private static final Logger LOGGER = LogManager.getLogger();
    private Map<Long, JobEntry> metaStore = Maps.newConcurrentMap();

    @Inject
    private JobMapper jobMapper;

    @Inject
    private JobScheduleExpressionMapper jobScheduleExpressionMapper;

    @Inject
    private JobDependMapper jobDependMapper;

    @Inject
    private AppMapper appMapper;

    @Inject
    private void init() {
        loadMetaDataFromDB();
        LOGGER.info("jobService loadMetaDataFromDB finished.");
    }

    public long insertJob(Job record) {
        // 1. insert to DB
        jobMapper.insertSelective(record);
        long jobId = record.getJobId();

        // 2. insert to cache
        Job newRecord = jobMapper.selectByPrimaryKey(jobId);
        JobEntry jobEntry = new JobEntry(newRecord, new ArrayList<>(), new HashMap<>());
        metaStore.put(jobId, jobEntry);

        return jobId;
    }

    public void updateJob(Job record) {
        // 1. update to DB
        jobMapper.updateByPrimaryKeySelective(record);

        // 2. update to cache
        long jobId = record.getJobId();
        JobEntry jobEntry = get(jobId);
        if (jobEntry != null) {
            Job newRecord = jobMapper.selectByPrimaryKey(jobId);
            jobEntry.setJob(newRecord);
        }
    }

    public void deleteJob(long jobId) {
        jobMapper.deleteByPrimaryKey(jobId);
        metaStore.remove(jobId);
    }

    public JobScheduleExpression getScheduleExpressionByJobId(long jobId) {
        JobScheduleExpressionExample example = new JobScheduleExpressionExample();
        example.createCriteria().andJobIdEqualTo(jobId);
        List<JobScheduleExpression> records = jobScheduleExpressionMapper.selectByExample(example);
        JobScheduleExpression record = null;
        if (records != null && !records.isEmpty()) {
            record = records.get(0);
        }
        return record;
    }

    public void insertScheduleExpression(long jobId, ScheduleExpressionEntry entry) {
        // 1. insert to DB
        JobScheduleExpression record = new JobScheduleExpression();
        record.setJobId(jobId);
        record.setExpressionType(entry.getExpressionType());
        record.setExpression(entry.getScheduleExpression());
        Date now = DateTime.now().toDate();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        jobScheduleExpressionMapper.insert(record);

        // 2. insert to cache
        ScheduleExpression scheduleExpression = null;
        int expressionType = record.getExpressionType();
        String expression = record.getExpression();
        if (expressionType == ScheduleExpressionType.CRON.getValue()) {
            scheduleExpression = new CronExpression(expression);
        } else if (expressionType == ScheduleExpressionType.FIXED_RATE.getValue()) {
            scheduleExpression = new FixedRateExpression(expression);
        } else if (expressionType == ScheduleExpressionType.FIXED_DELAY.getValue()) {
            scheduleExpression = new FixedDelayExpression(expression);
        } else if (expressionType == ScheduleExpressionType.ISO8601.getValue()) {
            scheduleExpression = new ISO8601Expression(expression);
        }
        List<ScheduleExpression> jobScheduleExpressions = Lists.newArrayList(scheduleExpression);
        JobEntry jobEntry = get(jobId);
        jobEntry.setScheduleExpressions(jobScheduleExpressions);
    }

    public void updateScheduleExpression(long jobId, ScheduleExpressionEntry entry) {
        // 1. update to DB
        JobScheduleExpressionExample example = new JobScheduleExpressionExample();
        example.createCriteria().andJobIdEqualTo(jobId);

        JobScheduleExpression record = new JobScheduleExpression();
        record.setExpressionType(entry.getExpressionType());
        record.setExpression(entry.getScheduleExpression());
        record.setUpdateTime(DateTime.now().toDate());
        jobScheduleExpressionMapper.updateByExampleSelective(record, example);

        // 2. update to cache
        ScheduleExpression scheduleExpression = null;
        int expressionType = record.getExpressionType();
        String expression = record.getExpression();
        if (expressionType == ScheduleExpressionType.CRON.getValue()) {
            scheduleExpression = new CronExpression(expression);
        } else if (expressionType == ScheduleExpressionType.FIXED_RATE.getValue()) {
            scheduleExpression = new FixedRateExpression(expression);
        } else if (expressionType == ScheduleExpressionType.FIXED_DELAY.getValue()) {
            scheduleExpression = new FixedDelayExpression(expression);
        } else if (expressionType == ScheduleExpressionType.ISO8601.getValue()) {
            scheduleExpression = new ISO8601Expression(expression);
        }
        List<ScheduleExpression> jobScheduleExpressions = Lists.newArrayList(scheduleExpression);
        JobEntry jobEntry = get(record.getJobId());
        jobEntry.setScheduleExpressions(jobScheduleExpressions);
    }

    public void deleteScheduleExpression(long jobId) {
        JobScheduleExpressionExample example = new JobScheduleExpressionExample();
        example.createCriteria().andJobIdEqualTo(jobId);
        jobScheduleExpressionMapper.deleteByExample(example);

        JobEntry jobEntry = get(jobId);
        jobEntry.setScheduleExpressions(new ArrayList<ScheduleExpression>());
    }

    public void insertJobDepend(JobDepend record) {
        jobDependMapper.insertSelective(record);

        JobDependencyEntry jobDependencyEntry = getJobDependencyEntry(record);
        JobEntry jobEntry = get(record.getJobId());
        jobEntry.addDependency(record.getPreJobId(), jobDependencyEntry);
    }

    public void deleteJobDepend(long jobId, long preJobId) {
        JobDependKey key = new JobDependKey();
        key.setJobId(jobId);
        key.setPreJobId(preJobId);
        jobDependMapper.deleteByPrimaryKey(key);

        JobEntry jobEntry = get(key.getJobId());
        jobEntry.removeDependency(key.getPreJobId());
    }

    public void deleteJobDependByPreJob(long preJobId) {
        JobDependExample jobDependExample = new JobDependExample();
        jobDependExample.createCriteria().andPreJobIdEqualTo(preJobId);
        List<JobDepend> jobDependList = jobDependMapper.selectByExample(jobDependExample);
        jobDependMapper.deleteByExample(jobDependExample);

        if (jobDependList != null) {
            for (JobDepend jobDepend : jobDependList) {
                JobEntry jobEntry = get(jobDepend.getJobId());
                jobEntry.removeDependency(preJobId);
            }
        }
    }

    public JobDepend getJobDepend(JobDependKey key) {
        return jobDependMapper.selectByPrimaryKey(key);
    }

    public void updateJobDepend(JobDepend record) {
        jobDependMapper.updateByPrimaryKey(record);

        JobDependencyEntry jobDependencyEntry = getJobDependencyEntry(record);
        JobEntry jobEntry = get(record.getJobId());
        jobEntry.updateDependency(record.getPreJobId(), jobDependencyEntry);
    }

    public JobEntry get(long jobId) {
        return metaStore.get(jobId);
    }

    public Map<Long, JobEntry> getMetaStore() {
        return metaStore;
    }

    public List<Job> getNotDeletedJobs() {
        JobExample example = new JobExample();
        example.createCriteria().andStatusNotEqualTo(JobStatus.DELETED.getValue());
        List<Job> jobs = jobMapper.selectByExampleWithBLOBs(example);
        if (jobs == null) {
            jobs = new ArrayList<Job>();
        }
        return jobs;
    }

    public boolean isActive(long jobId) {
        Job job = get(jobId).getJob();
        Date startDate = job.getActiveStartDate();
        Date endDate = job.getActiveEndDate();
        Date now = DateTime.now().toDate();
        if ((startDate == null || now.after(startDate)) && (endDate == null || now.before(endDate))) {
            return true;
        } else {
            return false;
        }
    }

    public void updateStatus(long jobId, String user, int status) {
        Job record = jobMapper.selectByPrimaryKey(jobId);
        record.setStatus(status);
        record.setUpdateUser(user);
        record.setUpdateTime(DateTime.now().toDate());
        jobMapper.updateByPrimaryKey(record);

        JobEntry jobEntry = get(jobId);
        jobEntry.updateJobStatus(status);
    }

    public String getAppName(long jobId) {
        App app = appMapper.selectByPrimaryKey(get(jobId).getJob().getAppId());
        return app.getAppName();
    }

    /**
     * 读取metaData
     */
    @Transactional
    private void loadMetaDataFromDB() {
        List<Job> jobs = getNotDeletedJobs();
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
            } else {
                LOGGER.warn("ExpressionType is undefined. id={};type={}", jobId, expressionType);
                continue;
            }

            if (!scheduleExpression.isValid()) {
                LOGGER.warn("expression value is invalid. id={};value={}", jobId, scheduleExpression.toString());
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
            Map<Long, JobDependencyEntry> dependencies = Maps.newHashMap();
            Collection<JobDepend> jobDependsCollection = jobDependMap.get(jobId);
            if (jobDependsCollection != null && jobDependsCollection.size() > 0) {
                for (JobDepend jobDepend : jobDependsCollection) {
                    JobDependencyEntry jobDependencyEntry = getJobDependencyEntry(jobDepend);
                    if (jobDependencyEntry != null) {
                        dependencies.put(jobDepend.getPreJobId(), jobDependencyEntry);
                    }
                }
            }

            // 初始化 JobMetaStore
            metaStore.put(job.getJobId(), new JobEntry(job, jobScheduleExpressions, dependencies));
        }
    }

    private JobDependencyEntry getJobDependencyEntry(JobDepend jobDepend) {
        long jobId = jobDepend.getJobId();
        JobDependencyEntry jobDependencyEntry = null;
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
        DependencyExpression dependencyExpression = null;
        if (offsetStrategy != null) {
            dependencyExpression = new TimeOffsetExpression(offsetStrategy);
            if (!dependencyExpression.isValid()) {
                LOGGER.warn("dependency expression is invalid. id={}; value={}", jobId, dependencyExpression.toString());
                return jobDependencyEntry;
            }
        }

        // 检查依赖策略表达式是否有效
        DependencyStrategyExpression dependencyStrategyExpression = new DefaultDependencyStrategyExpression(commonStrategyStr);
        if (!dependencyStrategyExpression.isValid()) {
            LOGGER.warn("dependency strategy is invalid. id={}; value={}", jobId, dependencyStrategyExpression.toString());
            return jobDependencyEntry;
        }

        jobDependencyEntry = new JobDependencyEntry(dependencyExpression, dependencyStrategyExpression);
        return jobDependencyEntry;
    }

    @VisibleForTesting
    public void deleteJobAndRelation(long jobId) {
        deleteJob(jobId);
        deleteJobDependByPreJob(jobId);
        deleteScheduleExpression(jobId);
    }

}