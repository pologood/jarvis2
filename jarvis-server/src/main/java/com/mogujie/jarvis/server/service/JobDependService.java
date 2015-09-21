/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月14日 上午11:52:16
 */

package com.mogujie.jarvis.server.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.dao.JobDependMapper;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetStrategyEnum;

/**
 * @author guangming
 *
 */
@Service
public class JobDependService {
    @Autowired
    private JobDependMapper jobDependMapper;

    public CommonStrategy getCommonStrategy(long myJobId, long preJobId) {
        return CommonStrategy.ALL;
    }

    public OffsetStrategyEnum getOffsetStrategyEnum(long myJobId, long preJobId) {
        return null;
    }

    public int getOffsetValue(long myJobId, long preJobId) {
        return 0;
    }

    public void deleteByJobId(long jobId) {

    }

    public Set<Long> getDependIds(long jobId) {
        return null;
    }
}
