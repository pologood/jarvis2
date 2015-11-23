/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月5日 下午2:17:54
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.server.scheduler.time.JobMetaStore.JobMetaStoreEntry;

/**
 * 
 *
 */
public enum JobGraph {
    INSTANCE;

    private DirectedGraph<Long, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private CycleDetector<Long, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
    private JobMetaStore jobMetaStore = JobMetaStore.INSTANCE;

    public boolean addJob(long jobId) {
        JobMetaStoreEntry jobMetaStoreEntry = jobMetaStore.get(jobId);
        List<ScheduleExpression> expressions = jobMetaStoreEntry.getScheduleExpressions();
        Map<Long, JobDependencyEntry> dependencies = jobMetaStoreEntry.getDependencies();

        // 没有调度时间表达式并且没有依赖的Job不准添加
        if ((expressions == null || expressions.size() == 0) && (dependencies == null || dependencies.size() == 0)) {
            return false;
        }

        synchronized (graph) {
            graph.addVertex(jobId);
            if (dependencies != null) {
                for (Long dependencyJobId : dependencies.keySet()) {
                    if (!graph.containsVertex(dependencyJobId)) {
                        graph.addVertex(dependencyJobId);
                    }
                    graph.addEdge(dependencyJobId, jobId);
                }

                if (cycleDetector.detectCycles()) {
                    graph.removeVertex(jobId);
                    return false;
                }
            }
        }

        return true;
    }

    public void removeJob(long jobId) {
        synchronized (graph) {
            if (graph.containsVertex(jobId)) {
                graph.removeVertex(jobId);
            }
        }
    }

    public DirectedGraph<Long, DefaultEdge> getGraph() {
        return graph;
    }

    public DateTime getScheduleTimeAfter(long jobId, DateTime dateTime) {
        MutableDateTime result = null;
        JobMetaStoreEntry jobMetaStoreEntry = jobMetaStore.get(jobId);
        List<ScheduleExpression> expressions = jobMetaStoreEntry.getScheduleExpressions();
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

        synchronized (graph) {
            Set<DefaultEdge> incomingEdges = graph.incomingEdgesOf(jobId);
            for (DefaultEdge edge : incomingEdges) {
                long dependencyJobId = graph.getEdgeSource(edge);
                DependencyExpression dependencyExpression = jobMetaStoreEntry.getDependencies().get(dependencyJobId).getDependencyExpression();
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
        }

        return result.toDateTime();
    }

}
