/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 下午2:39:43
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.ReflectionUtils;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.server.domain.JobKey;
import com.mogujie.jarvis.server.scheduler.dag.strategy.AbstractOffsetStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetStrategyFactory;
import com.mogujie.jarvis.server.service.JobDependService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class DependStatusFactory {
    public static final String JOB_DEPEND_STATUS_KEY = "job.depend.status";
    public static final String DEFAULT_JOB_DEPEND_STATUS = MysqlCachedDependStatus.class.getName();

    public static AbstractDependStatus create(JobKey myJobKey, JobKey preJobKey) throws ClassNotFoundException {
        AbstractDependStatus dependStatus = null;
        JobDependService jobDependService = SpringContext.getBean(JobDependService.class);
        if (jobDependService != null) {
            JobDepend jobDepend = jobDependService.getRecord(myJobKey.getJobId(), preJobKey.getJobId());
            if (jobDepend != null) {
                CommonStrategy commonStrategy = CommonStrategy.getInstance(jobDepend.getCommonStrategy());
                Pair<AbstractOffsetStrategy, Integer> offsetStrategyPair = OffsetStrategyFactory.create(jobDepend.getOffsetStrategy());
                if (offsetStrategyPair != null) {
                    dependStatus = new OffsetDependStatus(myJobKey, preJobKey, commonStrategy, offsetStrategyPair.getFirst(),
                            offsetStrategyPair.getSecond());
                } else {
                    Configuration conf = ConfigUtils.getServerConfig();
                    String className = conf.getString(JOB_DEPEND_STATUS_KEY, DEFAULT_JOB_DEPEND_STATUS);
                    dependStatus = ReflectionUtils.getInstanceByClassName(className);
                    dependStatus.setMyJobKey(myJobKey);
                    dependStatus.setPreJobKey(preJobKey);
                    dependStatus.setCommonStrategy(commonStrategy);
                }
            }
        }

        return dependStatus;
    }
}
