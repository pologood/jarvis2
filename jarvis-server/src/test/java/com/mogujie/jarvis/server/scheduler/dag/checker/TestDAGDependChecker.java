/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年1月6日 上午9:31:05
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.domain.TaskType;
import com.mogujie.jarvis.core.expression.DefaultDependencyStrategyExpression;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.DependencyStrategyExpression;
import com.mogujie.jarvis.core.expression.TimeOffsetExpression;
import com.mogujie.jarvis.server.domain.CommonStrategy;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.TaskService;

/**
 * @author guangming
 *
 */
public class TestDAGDependChecker {
    private long jobAId = 1;
    private long jobBId = 2;
    private long jobCId = 3;
    private long t1 = new DateTime("2015-10-10T10:10:00").getMillis();
    private long t2 = new DateTime("2015-10-11T10:10:00").getMillis();
    private long t3 = new DateTime("2015-10-12T10:10:00").getMillis();
    private TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);

    /**
    *   A   B
    *    \ /
    *     C
    */
    @Test
    public void testCurrentDayAll() {
        long taskAId = taskService.createTaskByJobId(jobAId, t1, t1, TaskType.SCHEDULE);
        long taskBId = taskService.createTaskByJobId(jobBId, t1, t1, TaskType.SCHEDULE);

        DAGDependChecker checker = new DAGDependChecker(jobCId);
        Map<Long, JobDependStatus> jobDependMap = Maps.newHashMap();
        DependencyExpression dependencyExpression = new TimeOffsetExpression("cd");
        DependencyStrategyExpression dependencyStrategy = new DefaultDependencyStrategyExpression(CommonStrategy.ALL.getExpression());
        JobDependStatus statusC2A = new JobDependStatus(jobCId, jobAId, dependencyExpression, dependencyStrategy);
        JobDependStatus statusC2B = new JobDependStatus(jobCId, jobBId, dependencyExpression, dependencyStrategy);
        jobDependMap.put(jobAId, statusC2A);
        jobDependMap.put(jobBId, statusC2B);
        checker.setJobDependMap(jobDependMap);

        long scheduleTime = new DateTime("2015-10-10T11:11:00").getMillis();
        Assert.assertEquals(false, checker.checkDependency(Sets.newHashSet(jobAId, jobBId), scheduleTime));

        taskService.updateStatus(taskAId, TaskStatus.SUCCESS);
        Assert.assertEquals(false, checker.checkDependency(Sets.newHashSet(jobAId, jobBId), scheduleTime));

        taskService.updateStatus(taskBId, TaskStatus.SUCCESS);
        Assert.assertEquals(true, checker.checkDependency(Sets.newHashSet(jobAId, jobBId), scheduleTime));

        taskService.deleteTaskAndRelation(taskAId);
        taskService.deleteTaskAndRelation(taskBId);
    }
}
