/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 下午2:39:43
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.common.util.ReflectionUtils;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.IOffsetDependStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetStrategyEnum;
import com.mogujie.jarvis.server.service.JobDependService;

/**
 * @author guangming
 *
 */
public class DependStatusFactory {
    public static String JOB_DEPEND_STATUS_KEY = "job.depend.status";
    public static String DEFAULT_JOB_DEPEND_STATUS = MysqlCachedDependStatus.class.getName();

    public static AbstractDependStatus createDependStatus(JobDependService jobDependService,
            long myJobId, long preJobId) throws ClassNotFoundException {
        AbstractDependStatus dependStatus;
        CommonStrategy commonStrategy = jobDependService.getCommonStrategy(myJobId, preJobId);
        OffsetStrategyEnum offsetStrategyEnum = jobDependService.getOffsetStrategyEnum(myJobId, preJobId);
        if (offsetStrategyEnum != null) {
            String className = offsetStrategyEnum.getValue();
            IOffsetDependStrategy offsetDependStrategy = ReflectionUtils.getInstanceByClassName(className);
            int offset = jobDependService.getOffsetValue(myJobId, preJobId);
            dependStatus = new OffsetDependStatus(myJobId, preJobId, commonStrategy,
                    offsetDependStrategy, offset);
        } else {
            Configuration conf = ConfigUtils.getServerConfig();
            String className = conf.getString(JOB_DEPEND_STATUS_KEY, DEFAULT_JOB_DEPEND_STATUS);
            dependStatus = ReflectionUtils.getInstanceByClassName(className);
        }

        return dependStatus;
    }
}
