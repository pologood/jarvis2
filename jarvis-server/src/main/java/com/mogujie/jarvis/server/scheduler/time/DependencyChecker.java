/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月5日 下午7:29:07
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.joda.time.DateTime;

import com.google.common.collect.BoundType;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Table;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.DependencyStrategyExpression;
import com.mogujie.jarvis.server.scheduler.time.JobMetaStore.JobMetaStoreEntry;

/**
 * 
 *
 */
public enum DependencyChecker {

    INSTANCE;

    private JobGraph jobGraph = JobGraph.INSTANCE;
    private JobMetaStore jobMetaStore = JobMetaStore.INSTANCE;
    private Table<Pair<Long, DateTime>, Pair<Long, DateTime>, Boolean> dependencyTable = HashBasedTable.create();

    /**
     * 检查依赖是否满足
     * 
     * @param pair
     * @return
     */
    public synchronized boolean checkDependency(Pair<Long, DateTime> pair) {
        JobMetaStoreEntry jobMetaStoreEntry = jobMetaStore.get(pair.getFirst());
        Map<Long, JobDependencyEntry> jobDependencyMap = jobMetaStoreEntry.getDependencies();
        Map<Pair<Long, DateTime>, Boolean> rows = dependencyTable.row(pair);
        if (rows.size() == 0) {
            return true;
        }

        Map<Long, List<Boolean>> dependencyStatusMap = Maps.newHashMap();
        for (Entry<Pair<Long, DateTime>, Boolean> entry : rows.entrySet()) {
            long dependencyJobId = entry.getKey().getFirst();
            List<Boolean> statusList = dependencyStatusMap.get(dependencyJobId);
            if (statusList == null) {
                statusList = Lists.newArrayList();
                dependencyStatusMap.put(dependencyJobId, statusList);
            }
            statusList.add(entry.getValue());
        }

        for (Entry<Long, List<Boolean>> entry : dependencyStatusMap.entrySet()) {
            long dependencyJobId = entry.getKey();
            JobDependencyEntry jobDependencyEntry = jobDependencyMap.get(dependencyJobId);
            DependencyStrategyExpression strategyExpression = jobDependencyEntry.getDependencyStrategyExpression();
            if (!strategyExpression.check(entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 当依赖任务完成后,更新依赖状态
     * 
     * @param pair
     * @param status
     * @return
     */
    public synchronized List<Pair<Long, DateTime>> updateDependency(Pair<Long, DateTime> pair, boolean status) {
        List<Pair<Long, DateTime>> result = null;
        Map<Pair<Long, DateTime>, Boolean> columns = dependencyTable.column(pair);
        for (Entry<Pair<Long, DateTime>, Boolean> columnEntry : columns.entrySet()) {
            Pair<Long, DateTime> rowKey = columnEntry.getKey();
            dependencyTable.put(rowKey, pair, status);
            if (checkDependency(rowKey)) {
                if (result == null) {
                    result = Lists.newArrayList();
                }
                result.add(rowKey);
                Map<Pair<Long, DateTime>, Boolean> rows = dependencyTable.row(rowKey);
                for (Entry<Pair<Long, DateTime>, Boolean> rowEntry : rows.entrySet()) {
                    dependencyTable.remove(rowKey, rowEntry.getKey());
                }
            }
        }

        return result;
    }

    /**
     * 添加任务，
     * 
     * @param jobId
     * @param scheduleTime
     */
    public void addJob(long jobId, DateTime scheduleTime) {
        Pair<Long, DateTime> jobPair = new Pair<Long, DateTime>(jobId, scheduleTime);
        DirectedGraph<Long, DefaultEdge> directedGraph = jobGraph.getGraph();
        Set<DefaultEdge> edges = directedGraph.incomingEdgesOf(jobId);
        if (edges.size() != 0) {
            JobMetaStoreEntry jobMetaStoreEntry = jobMetaStore.get(jobId);
            Map<Long, JobDependencyEntry> jobDependencyMap = jobMetaStoreEntry.getDependencies();
            for (DefaultEdge edge : edges) {
                long dependencyJobId = directedGraph.getEdgeSource(edge);
                DependencyExpression dependencyExpression = jobDependencyMap.get(dependencyJobId).getDependencyExpression();
                Range<DateTime> dateTimeRange = dependencyExpression.getRange(scheduleTime);
                if (dateTimeRange != null) {
                    DateTime startDateTime = dateTimeRange.lowerBoundType() == BoundType.OPEN ? dateTimeRange.lowerEndpoint()
                            : dateTimeRange.lowerEndpoint().minusSeconds(1);
                    DateTime endDateTime = dateTimeRange.upperBoundType() == BoundType.OPEN ? dateTimeRange.upperEndpoint()
                            : dateTimeRange.upperEndpoint().plusSeconds(1);
                    DateTime nextDateTime = jobGraph.getScheduleTimeAfter(dependencyJobId, startDateTime);
                    while (nextDateTime.isBefore(endDateTime)) {
                        synchronized (dependencyTable) {
                            Pair<Long, DateTime> columnKey = new Pair<Long, DateTime>(dependencyJobId, nextDateTime);
                            if (!dependencyTable.contains(jobPair, columnKey)) {
                                dependencyTable.put(jobPair, columnKey, false);
                            }
                        }
                        nextDateTime = jobGraph.getScheduleTimeAfter(dependencyJobId, nextDateTime);
                    }
                }
            }
        }
    }

}
