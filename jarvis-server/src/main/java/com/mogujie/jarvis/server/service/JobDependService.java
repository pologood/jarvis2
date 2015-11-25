/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月14日 上午11:52:16
 */

package com.mogujie.jarvis.server.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.dao.generate.JobDependMapper;
import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.JobDependExample;
import com.mogujie.jarvis.dto.generate.JobDependKey;

/**
 * @author guangming
 *
 */
@Service
public class JobDependService {
    @Autowired
    private JobDependMapper jobDependMapper;

    public JobDepend getRecord(long myJobId, long preJobId) {
        JobDependKey key = new JobDependKey();
        key.setJobId(myJobId);
        key.setPreJobId(preJobId);
        return jobDependMapper.selectByPrimaryKey(key);
    }

    public void deleteByJobId(long jobId) {
        JobDependExample example = new JobDependExample();
        example.createCriteria().andJobIdEqualTo(jobId);
        jobDependMapper.deleteByExample(example);
    }

    public Set<Long> getDependIds(long jobId) {
        JobDependExample example = new JobDependExample();
        example.createCriteria().andJobIdEqualTo(jobId);
        List<JobDepend> jobDepends = jobDependMapper.selectByExample(example);
        Set<Long> dependIds = Sets.newHashSet();
        if (jobDepends != null) {
            for (JobDepend jobDepend : jobDepends) {
                dependIds.add(jobDepend.getPreJobId());
            }
        }
        return dependIds;
    }
}
