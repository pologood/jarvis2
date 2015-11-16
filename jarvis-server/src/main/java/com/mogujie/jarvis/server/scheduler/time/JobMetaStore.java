/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月5日 下午3:42:24
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.dto.Job;

/**
 * 
 *
 */
public enum JobMetaStore {
    INSTANCE;

    private Map<Long, JobMetaStoreEntry> metaStore = Maps.newConcurrentMap();

    public void addJob(Job job, List<ScheduleExpression> scheduleExpressions, Map<Long, JobDependencyEntry> dependencies) {
        metaStore.put(job.getJobId(), new JobMetaStoreEntry(job, scheduleExpressions, dependencies));
    }

    public JobMetaStoreEntry get(long jobId) {
        return metaStore.get(jobId);
    }

    public void remove(long jobId) {
        metaStore.remove(jobId);
    }

    public class JobMetaStoreEntry {
        private final Job job;
        private final List<ScheduleExpression> scheduleExpressions;
        private final Map<Long, JobDependencyEntry> dependencies;

        public JobMetaStoreEntry(Job job, List<ScheduleExpression> scheduleExpressions, Map<Long, JobDependencyEntry> dependencies) {
            this.job = job;
            this.scheduleExpressions = scheduleExpressions;
            this.dependencies = dependencies;
        }

        public Job getJob() {
            return job;
        }

        public List<ScheduleExpression> getScheduleExpressions() {
            return scheduleExpressions;
        }

        public Map<Long, JobDependencyEntry> getDependencies() {
            return dependencies;
        }

    }
}
