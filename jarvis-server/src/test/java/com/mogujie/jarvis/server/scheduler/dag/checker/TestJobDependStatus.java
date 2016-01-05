/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年1月5日 下午6:57:00
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

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
public class TestJobDependStatus {
    private long jobAId = 1;
    private long jobBId = 2;
    private long t1 = new DateTime("2015-10-10T10:10:00").getMillis();
    private long t2 = new DateTime("2015-10-11T10:10:00").getMillis();
    private long t3 = new DateTime("2015-10-12T10:10:00").getMillis();
    private TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);

    @Test
    public void testCurrentDayAll() {
        long taskAId = taskService.createTaskByJobId(jobAId, t1, t1, TaskType.SCHEDULE);

        DependencyExpression dependencyExpression = new TimeOffsetExpression("cd");
        DependencyStrategyExpression dependencyStrategy = new DefaultDependencyStrategyExpression(CommonStrategy.ALL.getExpression());
        JobDependStatus status = new JobDependStatus(jobBId, jobAId, dependencyExpression, dependencyStrategy);

        // taskAId is waiting, check failed
        long scheduleTime = new DateTime("2015-10-10T11:11:00").getMillis();
        Assert.assertEquals(false, status.check(scheduleTime));

        // taskAId is finished, check success
        taskService.updateStatus(taskAId, TaskStatus.SUCCESS);
        Assert.assertEquals(true, status.check(scheduleTime));

        // can't find one task on 2015-10-11, check failed
        scheduleTime = new DateTime("2015-10-11T11:11:00").getMillis();
        Assert.assertEquals(false, status.check(scheduleTime));

        taskService.deleteTaskAndRelation(taskAId);
    }

    @Test
    public void testCurrentDayAnyone() {

    }

    @Test
    public void testCurrentDayLastone() {

    }

    @Test
    public void testCurrentHourAll() {

    }

    @Test
    public void testLast3DayAll() {

    }

}
