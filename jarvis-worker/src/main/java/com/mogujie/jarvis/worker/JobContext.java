/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午2:54:51
 */

package com.mogujie.jarvis.worker;

import java.util.Map;

/**
 * @author wuya
 *
 */
public class JobContext {

  private long jobId;
  private String fullId;
  private String jobName;
  private String cronExpression;
  private long[] dependencyJobids;
  private String appName;
  private String appKey;
  private String user;
  private String jobType;
  private String command;
  private int groupId;
  private int priority;
  private int rejectRetries;
  private int rejectInterval;
  private int failedRetries;
  private int failedInterval;
  private String startTime;
  private String endTime;
  private Map<String, Object> parameters;

  private AbstractLogCollector logCollector;
  private ProgressReporter progressReporter;

  private JobContext() {
  }

  public static JobContextBuilder newBuilder() {
    return new JobContextBuilder();
  }

  public long getJobId() {
    return jobId;
  }

  public String getFullId() {
    return fullId;
  }

  public String getJobName() {
    return jobName;
  }

  public String getCronExpression() {
    return cronExpression;
  }

  public long[] getDependencyJobids() {
    return dependencyJobids;
  }

  public String getAppName() {
    return appName;
  }

  public String getAppKey() {
    return appKey;
  }

  public String getUser() {
    return user;
  }

  public String getJobType() {
    return jobType;
  }

  public String getCommand() {
    return command;
  }

  public int getGroupId() {
    return groupId;
  }

  public int getPriority() {
    return priority;
  }

  public int getRejectRetries() {
    return rejectRetries;
  }

  public int getRejectInterval() {
    return rejectInterval;
  }

  public int getFailedRetries() {
    return failedRetries;
  }

  public int getFailedInterval() {
    return failedInterval;
  }

  public String getStartTime() {
    return startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public Map<String, Object> getParameters() {
    return parameters;
  }

  public AbstractLogCollector getLogCollector() {
    return logCollector;
  }

  public ProgressReporter getProgressReporter() {
    return progressReporter;
  }

  public static class JobContextBuilder {

    private JobContext jobContext;

    private JobContextBuilder() {
    }

    public JobContextBuilder setJobId(long jobId) {
      this.jobContext.jobId = jobId;
      return this;
    }

    public JobContextBuilder setFullId(String fullId) {
      this.jobContext.fullId = fullId;
      return this;
    }

    public JobContextBuilder setJobName(String jobName) {
      this.jobContext.jobName = jobName;
      return this;
    }

    public JobContextBuilder setCronExpression(String cronExpression) {
      this.jobContext.cronExpression = cronExpression;
      return this;
    }

    public JobContextBuilder setDependencyJobids(long[] dependencyJobids) {
      this.jobContext.dependencyJobids = dependencyJobids;
      return this;
    }

    public JobContextBuilder setAppName(String appName) {
      this.jobContext.appName = appName;
      return this;
    }

    public JobContextBuilder setAppKey(String appKey) {
      this.jobContext.appKey = appKey;
      return this;
    }

    public JobContextBuilder setUser(String user) {
      this.jobContext.user = user;
      return this;
    }

    public JobContextBuilder setJobType(String jobType) {
      this.jobContext.jobType = jobType;
      return this;
    }

    public JobContextBuilder setCommand(String command) {
      this.jobContext.command = command;
      return this;
    }

    public JobContextBuilder setGroupId(int groupId) {
      this.jobContext.groupId = groupId;
      return this;
    }

    public JobContextBuilder setPriority(int priority) {
      this.jobContext.priority = priority;
      return this;
    }

    public JobContextBuilder setRejectRetries(int rejectRetries) {
      this.jobContext.rejectRetries = rejectRetries;
      return this;
    }

    public JobContextBuilder setRejectInterval(int rejectInterval) {
      this.jobContext.rejectInterval = rejectInterval;
      return this;
    }

    public JobContextBuilder setFailedRetries(int failedRetries) {
      this.jobContext.failedRetries = failedRetries;
      return this;
    }

    public JobContextBuilder setFailedInterval(int failedInterval) {
      this.jobContext.failedInterval = failedInterval;
      return this;
    }

    public JobContextBuilder setStartTime(String startTime) {
      this.jobContext.startTime = startTime;
      return this;
    }

    public JobContextBuilder setEndTime(String endTime) {
      this.jobContext.endTime = endTime;
      return this;
    }

    public JobContextBuilder setParameters(Map<String, Object> parameters) {
      this.jobContext.parameters = parameters;
      return this;
    }

    public JobContextBuilder setLogCollector(AbstractLogCollector logCollector) {
      this.jobContext.logCollector = logCollector;
      return this;
    }

    public JobContextBuilder setProgressReporter(ProgressReporter progressReporter) {
      this.jobContext.progressReporter = progressReporter;
      return this;
    }

    public JobContext build() {
      return this.jobContext;
    }

  }

}
