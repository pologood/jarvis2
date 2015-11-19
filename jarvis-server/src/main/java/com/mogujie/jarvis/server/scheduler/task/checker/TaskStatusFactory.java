/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月5日 上午10:01:18
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import java.util.Map;

import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.DependencyStrategyExpression;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TaskStatusFactory {

    public static final String TASK_DEPEND_STATUS_KEY = "task.depend.status";
    public static final String DEFAULT_TASK_DEPEND_STATUS = RuntimeDependStatus.class.getName();
    private static final JobService jobService = SpringContext.getBean(JobService.class);

    public static AbstractTaskStatus create(long myJobId, long preJobId)  {
        AbstractTaskStatus dependStatus = null;
        //TODO jobDependService保留的是最新的依赖关系，如果修改过依赖关系，重跑历史任务时可能会无法找到对应的依赖关系
        // 可以考虑把依赖策略也保存在TaskDepend表中
        if (jobService != null) {
            Map<Long, JobDependencyEntry> dependencyMap = jobService.get(myJobId).getDependencies();
            if (dependencyMap != null && dependencyMap.containsKey(preJobId)) {
                JobDependencyEntry dependencyEntry = dependencyMap.get(preJobId);
                DependencyStrategyExpression commonStrategy = dependencyEntry.getDependencyStrategyExpression();
                DependencyExpression offsetExpression = dependencyEntry.getDependencyExpression();
                String expression = offsetExpression.getExpression();
                if (expression != null && !expression.startsWith("c")) {
                    dependStatus = new OffsetDependStatus(myJobId, preJobId, commonStrategy, offsetExpression);
                } else {
                    dependStatus = new RuntimeDependStatus(myJobId, preJobId, commonStrategy);
                }
            }
        }

        return dependStatus;
    }
}
