/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月14日 上午11:52:16
 */

package com.mogujie.jarvis.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.dao.JobDependMapper;

/**
 * @author guangming
 *
 */
@Service
public class JobDependService {
    @Autowired
    private JobDependMapper jobDependMapper;

    public void deleteByJobId(long jobId) {

    }
}
