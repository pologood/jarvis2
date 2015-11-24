/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月5日 下午5:42:41
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;

/**
 * 
 *
 */
public enum ExecutionPlanGenerator {
    INSTANCE;

    private ExecutionPlan plan = ExecutionPlan.INSTANCE;
    private JobGraph jobGraph = JobGraph.INSTANCE;
    private Map<Long, DateTime> cursorDateTimeMap = Maps.newConcurrentMap();

    /**
     * 生成下一次执行计划
     * 
     * @param jobId
     */
    public void generateNextPlan(long jobId) {
        DateTime cursorDateTime = cursorDateTimeMap.get(jobId);
        if (cursorDateTime == null) {
            cursorDateTime = jobGraph.getScheduleTimeAfter(jobId, DateTime.now());
        } else {
            cursorDateTime = jobGraph.getScheduleTimeAfter(jobId, cursorDateTime);
        }

        cursorDateTimeMap.put(jobId, cursorDateTime);
        plan.addPlan(jobId, cursorDateTime);
    }

    /**
     * 生成任务重跑执行计划
     * 
     * @param jobId
     * @param dateTimeRange
     */
    public void generateReschedulePlan(long jobId, Range<DateTime> dateTimeRange) {
        DateTime startDateTime = dateTimeRange.lowerEndpoint();
        DateTime endDatetTime = dateTimeRange.upperEndpoint();
        DateTime nextDateTime = jobGraph.getScheduleTimeAfter(jobId, startDateTime.minusSeconds(1));
        while (!nextDateTime.isBefore(startDateTime) && !nextDateTime.isAfter(endDatetTime)) {
            plan.addPlan(jobId, nextDateTime);
            nextDateTime = jobGraph.getScheduleTimeAfter(jobId, nextDateTime);
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
}
