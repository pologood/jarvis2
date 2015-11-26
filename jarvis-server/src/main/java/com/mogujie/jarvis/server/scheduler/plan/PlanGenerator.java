/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月5日 下午5:42:41
 */

package com.mogujie.jarvis.server.scheduler.plan;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.server.domain.JobEntry;
import com.mogujie.jarvis.server.scheduler.dag.JobGraph;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 *
 *
 */
public class PlanGenerator {

    protected ExecutionPlan plan = ExecutionPlan.INSTANCE;
    protected JobGraph jobGraph = JobGraph.INSTANCE;
    protected JobService jobService = SpringContext.getBean(JobService.class);

    /**
     * 生成任务重跑执行计划
     *
     * @param jobId
     * @param dateTimeRange
     */
    public void generateReschedulePlan(long jobId, Range<DateTime> dateTimeRange) {
        DateTime startDateTime = dateTimeRange.lowerEndpoint();
        DateTime endDatetTime = dateTimeRange.upperEndpoint();
        DateTime nextDateTime = getScheduleTimeAfter(jobId, startDateTime.minusSeconds(1));
        while (!nextDateTime.isBefore(startDateTime) && !nextDateTime.isAfter(endDatetTime)) {
            plan.addPlan(jobId, nextDateTime);
            nextDateTime = getScheduleTimeAfter(jobId, nextDateTime);
        }
    }

    /**
     * 批量生成任务重跑执行计划
     *
     * @param jobIds
     * @param dateTimeRange
     */
    public void generateReschedulePlan(List<Long> jobIds, Range<DateTime> dateTimeRange) {
        for (Long jobId : jobIds) {
            generateReschedulePlan(jobId, dateTimeRange);
        }
    }

    public void generateReschedulePlan(long jobId, DateTime dateTime) {
        Range<DateTime> range = Range.closed(dateTime, dateTime);
        generateReschedulePlan(jobId, range);
    }

    public void generateReschedulePlan(List<Long> jobIds, DateTime dateTime) {
        for (Long jobId : jobIds) {
            generateReschedulePlan(jobId, dateTime);
        }
    }

    public DateTime getScheduleTimeAfter(long jobId, DateTime dateTime) {
        MutableDateTime result = null;
        JobEntry jobEntry = jobService.get(jobId);
        List<ScheduleExpression> expressions = jobEntry.getScheduleExpressions();
        if (expressions != null && expressions.size() > 0) {
            for (ScheduleExpression scheduleExpression : expressions) {
                DateTime nextTime = scheduleExpression.getTimeAfter(dateTime);
                if (result == null) {
                    result = new MutableDateTime(nextTime);
                } else if (result.isAfter(nextTime)) {
                    result.setDate(nextTime);
                }
            }

            return result.toDateTime();
        }

        Set<Long> parentIds = jobGraph.getEnableParentJobIds(jobId);
        for (long dependencyJobId : parentIds) {
            DependencyExpression dependencyExpression = jobEntry.getDependencies().get(dependencyJobId).getDependencyExpression();
            if (dependencyExpression == null) {
                DateTime nextTime = getScheduleTimeAfter(dependencyJobId, dateTime);
                if (result == null) {
                    result = new MutableDateTime(nextTime);
                } else if (result.isBefore(nextTime)) {
                    result.setDate(nextTime);
                }
            } else {
                MutableDateTime mutableDateTime = dateTime.toMutableDateTime();
                while (true) {
                    Range<DateTime> dependencyRangeDateTime = dependencyExpression.getRange(mutableDateTime.toDateTime());
                    DateTime startDateTime = dependencyRangeDateTime.lowerBoundType() == BoundType.OPEN ? dependencyRangeDateTime.lowerEndpoint()
                            : dependencyRangeDateTime.lowerEndpoint().minusSeconds(1);
                    DateTime endDateTime = dependencyRangeDateTime.upperBoundType() == BoundType.OPEN ? dependencyRangeDateTime.upperEndpoint()
                            : dependencyRangeDateTime.upperEndpoint().plusSeconds(1);

                    DateTime nextTime = getScheduleTimeAfter(dependencyJobId, startDateTime);
                    while (nextTime.isBefore(endDateTime)) {
                        if (result == null) {
                            result = new MutableDateTime(nextTime);
                        } else if (result.isBefore(nextTime)) {
                            result.setDate(nextTime);
                        }
                        nextTime = getScheduleTimeAfter(dependencyJobId, nextTime);
                    }

                    if (!result.isAfter(dateTime)) {
                        mutableDateTime.setMillis(endDateTime);
                    } else {
                        break;
                    }
                }
            }
        }

        return result.toDateTime();
    }

    public void generateNextPlan(long jobId, DateTime dt) {
        DateTime nextTime = getScheduleTimeAfter(jobId, dt);
        if (nextTime != null) {
            plan.addPlan(jobId, nextTime);
        }
    }

    public void generateNextPlan() {}

    public long getPeriod() {
        return 0;
    }

}
