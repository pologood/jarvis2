/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月5日 下午5:42:41
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.server.scheduler.time.ExecutionPlan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.time.JobMetaStore.JobMetaStoreEntry;

/**
 * 
 *
 */
public enum ExecutionPlanGenerator {
    INSTANCE;

    private ExecutionPlan plan = ExecutionPlan.INSTANCE;
    private JobGraph jobGraph = JobGraph.INSTANCE;
    private JobMetaStore jobMetaStore = JobMetaStore.INSTANCE;
    private Map<Long, DateTime> cursorDateTimeMap = Maps.newConcurrentMap();

    private ExecutionPlanGenerator() {
    }

    /**
     * 初始化生成一天后的执行计划
     */
    public void init() {
        DateTime endDateTime = DateTime.now().plusDays(2).withTimeAtStartOfDay();
        SortedSet<ExecutionPlanEntry> planSet = plan.getPlan();
        Iterator<ExecutionPlanEntry> it = planSet.iterator();
        while (it.hasNext()) {
            ExecutionPlanEntry entry = it.next();
            long jobId = entry.getJobId();
            DateTime dateTime = entry.getDateTime();
            cursorDateTimeMap.put(jobId, dateTime);
        }

        DirectedGraph<Long, DefaultEdge> directedGraph = jobGraph.getGraph();
        for (Long jobId : directedGraph.vertexSet()) {
            DateTime cursorDateTime = cursorDateTimeMap.get(jobId);

            if (cursorDateTime != null && cursorDateTime.isAfter(endDateTime)) {
                continue;
            }

            DateTime startDateTime = null;
            if (cursorDateTime == null) {
                startDateTime = DateTime.now();
            } else {
                startDateTime = cursorDateTime;
            }

            DateTime nextDateTime = jobGraph.getScheduleTimeAfter(jobId, startDateTime);
            while (nextDateTime.isBefore(endDateTime)) {
                cursorDateTimeMap.put(jobId, nextDateTime);
                JobMetaStoreEntry jobMetaStoreEntry = jobMetaStore.get(jobId);
                Job job = jobMetaStoreEntry.getJob();
                DateTime activeStartDate = new DateTime(job.getActiveStartDate());
                DateTime activeEndDate = new DateTime(job.getActiveEndDate());
                if (activeStartDate != null && !nextDateTime.isBefore(activeEndDate) && activeEndDate != null
                        && !nextDateTime.isAfter(activeEndDate)) {
                    plan.addPlan(jobId, nextDateTime);
                    nextDateTime = jobGraph.getScheduleTimeAfter(jobId, nextDateTime);
                } else {
                    break;
                }
            }
        }
    }

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
