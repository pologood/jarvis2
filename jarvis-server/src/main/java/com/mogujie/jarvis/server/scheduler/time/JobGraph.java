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

import com.google.common.collect.Maps;
import com.mogujie.jarvis.core.expression.ScheduleExpression;

/**
 * 
 *
 */
public enum JobGraph {
    INSTANCE;

    private DirectedGraph<Long, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private CycleDetector<Long, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
    private Map<Long, List<ScheduleExpression>> expressionsMap = Maps.newConcurrentMap();

    public boolean addJob(long jobId, List<ScheduleExpression> expressions, List<Long> dependencyJobs) {
        // 没有调度时间表达式并且没有依赖的Job不准添加
        if ((expressions == null || expressions.size() == 0) && (dependencyJobs == null || dependencyJobs.size() == 0)) {
            return false;
        }

        synchronized (graph) {
            graph.addVertex(jobId);
            if (dependencyJobs != null) {
                for (Long dependencyJobId : dependencyJobs) {
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

        expressionsMap.put(jobId, expressions);
        return true;
    }

    public void removeJob(long jobId) {
        synchronized (graph) {
            if (graph.containsVertex(jobId)) {
                graph.removeVertex(jobId);
                expressionsMap.remove(jobId);
            }
        }
    }

    public DirectedGraph<Long, DefaultEdge> getGraph() {
        return graph;
    }

    public DateTime getScheduleTimeAfter(long jobId, DateTime dateTime) {
        MutableDateTime result = null;
        List<ScheduleExpression> expressions = expressionsMap.get(jobId);
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
                DateTime nextTime = getScheduleTimeAfter(dependencyJobId, dateTime);
                if (result == null) {
                    result = new MutableDateTime(nextTime);
                } else if (result.isAfter(nextTime)) {
                    result.setDate(nextTime);
                }
            }
        }

        return result.toDateTime();
    }

}
