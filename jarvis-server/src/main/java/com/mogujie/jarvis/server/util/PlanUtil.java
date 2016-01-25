/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月5日 下午5:42:41
 */

package com.mogujie.jarvis.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import com.google.common.collect.BoundType;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.server.domain.JobEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.dag.JobGraph;
import com.mogujie.jarvis.server.scheduler.time.TimePlanEntry;
import com.mogujie.jarvis.server.service.JobService;

public class PlanUtil {

    private static JobGraph jobGraph = JobGraph.INSTANCE;
    private static JobService jobService = Injectors.getInjector().getInstance(JobService.class);

    public static List<TimePlanEntry> getReschedulePlan(long jobId, Range<DateTime> dateTimeRange) {
        List<TimePlanEntry> planList = new ArrayList<TimePlanEntry>();
        DateTime startDateTime = dateTimeRange.lowerEndpoint();
        DateTime endDatetTime = dateTimeRange.upperEndpoint();
        DateTime nextDateTime = getScheduleTimeAfter(jobId, startDateTime.minusSeconds(1));
        while (!nextDateTime.isBefore(startDateTime) && !nextDateTime.isAfter(endDatetTime)) {
            planList.add(new TimePlanEntry(jobId, nextDateTime));
            nextDateTime = getScheduleTimeAfter(jobId, nextDateTime);
        }
        return planList;
    }

    public static Map<Long, List<TimePlanEntry>> getReschedulePlan(List<Long> jobIds, Range<DateTime> dateTimeRange) {
        Map<Long, List<TimePlanEntry>> planMap = Maps.newHashMap();
        for (Long jobId : jobIds) {
            planMap.put(jobId, getReschedulePlan(jobId, dateTimeRange));
        }
        return planMap;
    }

    public static DateTime getScheduleTimeAfter(long jobId, DateTime dateTime) {
        DateTime result = null;
        JobEntry jobEntry = jobService.get(jobId);
        Map<Long, ScheduleExpression> expressions = jobEntry.getScheduleExpressions();
        if (expressions != null && !expressions.isEmpty()) {
            for (ScheduleExpression scheduleExpression : expressions.values()) {
                DateTime nextTime = scheduleExpression.getTimeAfter(dateTime);
                if (result == null || result.isAfter(nextTime)) {
                    result = nextTime;
                }
            }
            return result;
        }

        Set<Long> parentIds = jobGraph.getParentJobIds(jobId);
        for (long dependencyJobId : parentIds) {
            DependencyExpression dependencyExpression = jobEntry.getDependencies().get(dependencyJobId).getDependencyExpression();
            if (dependencyExpression == null) {
                DateTime nextTime = getScheduleTimeAfter(dependencyJobId, dateTime);
                if (result == null || result.isBefore(nextTime)) {
                    result = nextTime;
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
                        if (result == null || result.isBefore(nextTime)) {
                            result = nextTime;
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

        return result;
    }

    public static DateTime getScheduleTimeBefore(long jobId, DateTime dateTime) {
        DateTime result = null;
        JobEntry jobEntry = jobService.get(jobId);
        Map<Long, ScheduleExpression> expressions = jobEntry.getScheduleExpressions();
        if (expressions != null && !expressions.isEmpty()) {
            for (ScheduleExpression scheduleExpression : expressions.values()) {
                DateTime lastTime = scheduleExpression.getTimeBefore(dateTime);
                if (result == null || result.isBefore(lastTime)) {
                    result = lastTime;
                }
            }
            return result;
        }
        return result;
    }

}
