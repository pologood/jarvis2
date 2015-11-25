package com.mogujie.jarvis.server.domain;

import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.dto.generate.Job;

import java.util.List;
import java.util.Map;

/**
 * Created by muming on 15/11/16.
 */
public class JobEntry {

    private final Job job;
    private final List<ScheduleExpression> scheduleExpressions;
    private final Map<Long, JobDependencyEntry> dependencies;

    public JobEntry(Job job, List<ScheduleExpression> scheduleExpressions, Map<Long, JobDependencyEntry> dependencies) {
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
