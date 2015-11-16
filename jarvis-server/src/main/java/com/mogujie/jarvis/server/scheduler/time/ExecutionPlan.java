/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月5日 下午3:47:31
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.joda.time.DateTime;

/**
 * 
 *
 */
public enum ExecutionPlan {
    INSTANCE;

    private Comparator<ExecutionPlanEntry> comparator = new Comparator<ExecutionPlan.ExecutionPlanEntry>() {

        @Override
        public int compare(ExecutionPlanEntry o1, ExecutionPlanEntry o2) {
            return o1.dateTime.compareTo(o2.dateTime);
        }
    };

    private SortedSet<ExecutionPlanEntry> plan = new ConcurrentSkipListSet<>(comparator);

    public boolean addPlan(long jobId, DateTime dateTime) {
        return plan.add(new ExecutionPlanEntry(jobId, dateTime));
    }

    public boolean removePlan(long jobId, DateTime dateTime) {
        return plan.remove(new ExecutionPlanEntry(jobId, dateTime));
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

    public class ExecutionPlanEntry {

        private final long jobId;
        private final DateTime dateTime;

        public ExecutionPlanEntry(long jobId, DateTime dateTime) {
            this.jobId = jobId;
            this.dateTime = dateTime;
        }

        public long getJobId() {
            return jobId;
        }

        public DateTime getDateTime() {
            return dateTime;
        }

        @Override
        public int hashCode() {
            return Objects.hash(jobId, dateTime);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof ExecutionPlanEntry)) {
                return false;
            }

            ExecutionPlanEntry other = (ExecutionPlanEntry) obj;
            return Objects.equals(jobId, other.jobId) && Objects.equals(dateTime, other.dateTime);
        }

    }
}
