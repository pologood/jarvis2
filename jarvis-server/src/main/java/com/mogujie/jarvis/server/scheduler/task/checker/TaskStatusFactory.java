/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月5日 上午10:01:18
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.server.scheduler.depend.strategy.AbstractOffsetStrategy;
import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;
import com.mogujie.jarvis.server.scheduler.depend.strategy.OffsetStrategyFactory;
import com.mogujie.jarvis.server.service.JobDependService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TaskStatusFactory {

    public static final String TASK_DEPEND_STATUS_KEY = "task.depend.status";
    public static final String DEFAULT_TASK_DEPEND_STATUS = RuntimeDependStatus.class.getName();
    private static final JobDependService jobDependService = SpringContext.getBean(JobDependService.class);

    public static AbstractTaskStatus create(long myJobId, long preJobId)  {
        AbstractTaskStatus dependStatus = null;
        if (jobDependService != null) {
            JobDepend jobDepend = jobDependService.getRecord(myJobId, preJobId);
            if (jobDepend != null) {
                CommonStrategy commonStrategy = CommonStrategy.getInstance(jobDepend.getCommonStrategy());
                Pair<AbstractOffsetStrategy, Integer> offsetStrategyPair = OffsetStrategyFactory.create(jobDepend.getOffsetStrategy());
                if (offsetStrategyPair != null) {
                    dependStatus = new OffsetDependStatus(myJobId, preJobId, commonStrategy, offsetStrategyPair.getFirst(),
                            offsetStrategyPair.getSecond());
                } else {
                    dependStatus = new RuntimeDependStatus(myJobId, preJobId, commonStrategy);
                }
            }
        }

        return dependStatus;
    }
}
