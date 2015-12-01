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
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ArrayListMultimap;
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

    public long insertJob(Job record) {
        jobMapper.insert(record);
        return record.getJobId();
    }

    public void updateJob(Job record) {
        jobMapper.updateByPrimaryKeySelective(record);
    }

    public void deleteJob(long jobId) {
        jobMapper.deleteByPrimaryKey(jobId);
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
        JobScheduleExpression record = new JobScheduleExpression();
        record.setJobId(jobId);
        record.setExpressionType(entry.getExpressionType());
        record.setExpression(entry.getScheduleExpression());
        Date now = new Date();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        jobScheduleExpressionMapper.insert(record);
    }

    public void updateScheduleExpression(JobScheduleExpression record) {
        jobScheduleExpressionMapper.updateByPrimaryKey(record);
    }

    public void updateScheduleExpression(long jobId, ScheduleExpressionEntry entry) {
        JobScheduleExpression record = new JobScheduleExpression();
        record.setJobId(jobId);
        record.setExpressionType(entry.getExpressionType());
        record.setExpression(entry.getScheduleExpression());
        record.setUpdateTime(new Date());
        jobScheduleExpressionMapper.updateByPrimaryKey(record);
    }


    public void deleteScheduleExpression(long jobId) {
        JobScheduleExpressionExample example = new JobScheduleExpressionExample();
        example.createCriteria().andJobIdEqualTo(jobId);
        jobScheduleExpressionMapper.deleteByExample(example);
    }

    public void insertJobDepend(JobDepend record) {
        jobDependMapper.insert(record);
    }

    public void deleteJobDepend(JobDependKey key) {
        jobDependMapper.deleteByPrimaryKey(key);
    }

    /**
     * remove job depend where preJobId=jobId
     * @param jobId
     */
    public void deleteJobDependByPreJob(long jobId) {
        JobDependExample jobDependExample = new JobDependExample();
        jobDependExample.createCriteria().andPreJobIdEqualTo(jobId);
        jobDependMapper.deleteByExample(jobDependExample);
    }

    public JobDepend getJobDepend(JobDependKey key) {
        return jobDependMapper.selectByPrimaryKey(key);
    }

    public void updateJobDepend(JobDepend record) {
        jobDependMapper.updateByPrimaryKey(record);
    }

    public JobEntry get(long jobId) {
        return metaStore.get(jobId);
    }

    public Map<Long, JobEntry> getMetaStore(){
        return metaStore;
    }

    public List<Job> getNotDeletedJobs() {
        JobExample example = new JobExample();
        example.createCriteria().andJobFlagNotEqualTo(JobFlag.DELETED.getValue());
        return jobMapper.selectByExample(example);
    }

    public boolean isActive(long jobId) {
        Job job = get(jobId).getJob();
        Date startDate = job.getActiveStartDate();
        Date endDate = job.getActiveEndDate();
        Date now = new Date();
        if (now.after(startDate) && now.before(endDate)) {
            return true;
        } else {
            return false;
        }
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

    public String getAppName(long jobId) {
        App app = appMapper.selectByPrimaryKey(get(jobId).getJob().getAppId());
        return app.getAppName();
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
            Map<Long, JobDependencyEntry> dependencies = Maps.newHashMap();
            Collection<JobDepend> jobDependsCollection = jobDependMap.get(jobId);
            if (jobDependsCollection != null && jobDependsCollection.size() > 0) {
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
                    DependencyExpression dependencyExpression = null;
                    if (offsetStrategy != null) {
                        dependencyExpression = new TimeOffsetExpression(offsetStrategy);
                        if (!dependencyExpression.isValid()) {
                            LOGGER.warn("dependency expression is invalid. id={}; value={}",jobId,dependencyExpression.toString());
                            continue;
                        }
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
        }
    }
}