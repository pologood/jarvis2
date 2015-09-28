/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月15日 下午2:35:11
 */
package com.mogujie.jarvis.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public int updateJobStatus(long jobId, int status) {
        // TODO
        return 0;
    }

    public List<Job> getJobsNotDeleted() {
        JobExample example = new JobExample();
        example.createCriteria().andJobFlagNotEqualTo(JobFlag.DELETED.getValue());
        return jobMapper.selectByExample(example);
    }
}