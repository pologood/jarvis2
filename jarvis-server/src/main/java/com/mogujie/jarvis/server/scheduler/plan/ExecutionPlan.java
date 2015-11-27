/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月5日 下午3:47:31
 */

package com.mogujie.jarvis.server.scheduler.plan;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.joda.time.DateTime;

/**
 *
 *
 */
public enum ExecutionPlan {
    INSTANCE;

    private Comparator<ExecutionPlanEntry> comparator = new Comparator<ExecutionPlanEntry>() {

        @Override
        public int compare(ExecutionPlanEntry o1, ExecutionPlanEntry o2) {
            return o1.getDateTime().compareTo(o2.getDateTime());
        }
    };

    private SortedSet<ExecutionPlanEntry> plan = new ConcurrentSkipListSet<>(comparator);

    public boolean addPlan(long jobId, DateTime dateTime) {
        return plan.add(new ExecutionPlanEntry(jobId, dateTime));
    }

    public boolean addPlan(ExecutionPlanEntry entry) {
        return plan.add(entry);
    }

    public boolean removePlan(long jobId, DateTime dateTime) {
        return plan.remove(new ExecutionPlanEntry(jobId, dateTime));
    }

    public boolean removePlan(ExecutionPlanEntry planEntry) {
        return plan.remove(planEntry);
    }

    public void removePlan(long jobId) {
        Iterator<ExecutionPlanEntry> it = plan.iterator();
        while (it.hasNext()) {
            ExecutionPlanEntry entry = it.next();
            if (entry.getJobId() == jobId) {
                it.remove();
            }
        }
    }

    public SortedSet<ExecutionPlanEntry> getPlan() {
        return plan;
    }

}
