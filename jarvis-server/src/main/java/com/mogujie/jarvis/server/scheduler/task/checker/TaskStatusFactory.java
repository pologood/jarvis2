/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月5日 上午10:01:18
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.expression.DefaultDependencyStrategyExpression;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.DependencyStrategyExpression;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.server.domain.CommonStrategy;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TaskStatusFactory {

    public static final String TASK_DEPEND_STATUS_KEY = "task.depend.status";
    public static final String DUMMY_DEPEND_STATUS = "dummy";
    private static final JobService jobService = SpringContext.getBean(JobService.class);

    public static AbstractTaskStatus create(long myJobId, long preJobId, long scheduleTime)  {
        AbstractTaskStatus dependStatus = null;
        Configuration conf = ConfigUtils.getServerConfig();
        String statusType = conf.getString(TASK_DEPEND_STATUS_KEY);
        if (statusType.equalsIgnoreCase(DUMMY_DEPEND_STATUS)) {
            DependencyStrategyExpression commonStrategy = new DefaultDependencyStrategyExpression(CommonStrategy.ALL.getExpression());
            dependStatus = new RuntimeDependStatus(myJobId, preJobId, commonStrategy);
        } else if (jobService != null) {
            //TODO jobDependService保留的是最新的依赖关系，如果修改过依赖关系，重跑历史任务时可能会无法找到对应的依赖关系
            // 可以考虑把依赖策略也保存在TaskDepend表中
            Map<Long, JobDependencyEntry> dependencyMap = jobService.get(myJobId).getDependencies();
            if (dependencyMap != null && dependencyMap.containsKey(preJobId)) {
                JobDependencyEntry dependencyEntry = dependencyMap.get(preJobId);
                DependencyStrategyExpression commonStrategy = dependencyEntry.getDependencyStrategyExpression();
                DependencyExpression offsetExpression = dependencyEntry.getDependencyExpression();
                String expression = offsetExpression.getExpression();
                if (expression != null && !expression.startsWith("c")) {
                    dependStatus = new OffsetDependStatus(myJobId, preJobId, scheduleTime, commonStrategy, offsetExpression);
                } else {
                    dependStatus = new RuntimeDependStatus(myJobId, preJobId, commonStrategy);
                }
            }
        }

        return dependStatus;
    }
}
