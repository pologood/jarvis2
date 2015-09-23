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
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.IOffsetDependStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetStrategyEnum;
import com.mogujie.jarvis.server.service.JobDependService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class DependStatusFactory {
    public static String JOB_DEPEND_STATUS_KEY = "job.depend.status";
    public static String DEFAULT_JOB_DEPEND_STATUS = MysqlCachedDependStatus.class.getName();

    public static AbstractDependStatus create(long myJobId, long preJobId) throws ClassNotFoundException {
        AbstractDependStatus dependStatus = null;
        JobDependService jobDependService = SpringContext.getBean(JobDependService.class);
        if (jobDependService != null) {
            JobDepend jobDepend = jobDependService.getRecord(myJobId, preJobId);
            if (jobDepend != null) {
                CommonStrategy commonStrategy = CommonStrategy.getInstance(jobDepend.getCommonStrategy());
                String offsetStrategyStr = jobDepend.getOffsetStrategy();
                if (offsetStrategyStr != null && !offsetStrategyStr.isEmpty()) {
                    String offsetStrategyMap[] = offsetStrategyStr.split(":");
                    String offsetStrategyKey = offsetStrategyMap[0];
                    int offsetValue = Integer.valueOf(offsetStrategyMap[1]);
                    OffsetStrategyEnum offsetStrategyEnum = OffsetStrategyEnum.getInstance(offsetStrategyKey);
                    if (offsetStrategyEnum != null) {
                        String className = offsetStrategyEnum.getValue();
                        IOffsetDependStrategy offsetDependStrategy = ReflectionUtils.getInstanceByClassName(className);
                        dependStatus = new OffsetDependStatus(myJobId, preJobId, commonStrategy,
                                offsetDependStrategy, offsetValue);
                    }
                } else {
                    Configuration conf = ConfigUtils.getServerConfig();
                    String className = conf.getString(JOB_DEPEND_STATUS_KEY, DEFAULT_JOB_DEPEND_STATUS);
                    dependStatus = ReflectionUtils.getInstanceByClassName(className);
                    dependStatus.setMyjobId(myJobId);
                    dependStatus.setPreJobId(preJobId);
                    dependStatus.setCommonStrategy(commonStrategy);
                }
            }
        }

        return dependStatus;
    }
}
