/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月15日 下午2:35:11
 */
package com.mogujie.jarvis.server.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.JobExample;

/**
 * @author wuya
 *
 */
@Service
public class JobService {

    @Autowired
    private JobMapper jobMapper;

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

    public boolean hasFixedDelay(long jobId) {
        Job job = jobMapper.selectByPrimaryKey(jobId);
        Preconditions.checkNotNull(job, "Job " + jobId +" not found!");
        if (job.getFixedDelay() != null && job.getFixedDelay() > 0) {
            return true;
        }
        return false;
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
}