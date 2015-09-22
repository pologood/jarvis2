/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月10日 上午10:13:36
 */

package com.mogujie.jarvis.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.dao.JobDependStatusMapper;
import com.mogujie.jarvis.dto.JobDependStatus;
import com.mogujie.jarvis.dto.JobDependStatusKey;

/**
 * @author guangming
 *
 */
@Service
public class DependStatusService {
    @Autowired
    private JobDependStatusMapper dependStatusMapper;

    public JobDependStatus getByKey(JobDependStatusKey key) {
        return dependStatusMapper.selectByPrimaryKey(key);
    }

    public void insert(JobDependStatus record) {
        dependStatusMapper.insert(record);
    }

    public void update(JobDependStatus record) {
        dependStatusMapper.updateByPrimaryKey(record);
    }

    public void deleteDependencyByPreJobId(Long myJobId, long preJobId) {
        // TODO
    }

    public void clearMyStatus(long myJobId) {
        // TODO
    }

    public void clearPreStatus(long myJobId, long preJobId) {
        // TODO
    }

    public List<JobDependStatus> getRecordsByMyJobId(long myJobId) {
        // TODO
        return null;
    }

    public List<JobDependStatus> getRecordsByPreJobId(long myJobId, long preJobId) {
        // TODO
        return null;
    }
}
