/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月15日 下午4:46:47
 */

package com.mogujie.jarvis.core.domain;

import java.util.Map;

import org.joda.time.DateTime;

/**
 * 
 *
 */
public class TaskDetail {

    private String fullId;
    private String taskName;
    private String appName;
    private String user;
    private String taskType;
    private String content;
    private int priority;
    private int groupId;
    private Map<String, Object> parameters;
    private long schedulingTime;
    private int rejectInterval;
    private int rejectRetries;
    private int alreadyRetries;
    private DateTime nextRetryTime;

    private TaskDetail() {
    }

    public String getFullId() {
        return fullId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getAppName() {
        return appName;
    }

    public String getUser() {
        return user;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getContent() {
        return content;
    }

    public int getPriority() {
        return priority;
    }

    public int getGroupId() {
        return groupId;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public long getSchedulingTime() {
        return schedulingTime;
    }

    public int getRejectInterval() {
        return rejectInterval;
    }

    public int getRejectRetries() {
        return rejectRetries;
    }

    public int getAlreadyRetries() {
        return alreadyRetries;
    }

    public void setAlreadyRetries(int alreadyRetries) {
        this.alreadyRetries = alreadyRetries;
    }

    public DateTime getNextRetryTime() {
        return nextRetryTime;
    }

    public void setNextRetryTime(DateTime nextRetryTime) {
        this.nextRetryTime = nextRetryTime;
    }

    public static TaskDetailBuilder newTaskDetailBuilder() {
        return new TaskDetailBuilder();
    }

    public static class TaskDetailBuilder {

        private TaskDetail task = new TaskDetail();;

        private TaskDetailBuilder() {
        }

        public TaskDetailBuilder setFullId(String fullId) {
            this.task.fullId = fullId;
            return this;
        }

        public TaskDetailBuilder setTaskName(String taskName) {
            this.task.taskName = taskName;
            return this;
        }

        public TaskDetailBuilder setAppName(String appName) {
            this.task.appName = appName;
            return this;
        }

        public TaskDetailBuilder setUser(String user) {
            this.task.user = user;
            return this;
        }

        public TaskDetailBuilder setTaskType(String taskType) {
            this.task.taskType = taskType;
            return this;
        }

        public TaskDetailBuilder setContent(String content) {
            this.task.content = content;
            return this;
        }

        public TaskDetailBuilder setPriority(int priority) {
            this.task.priority = priority;
            return this;
        }

        public TaskDetailBuilder setGroupId(int groupId) {
            this.task.groupId = groupId;
            return this;
        }

        public TaskDetailBuilder setParameters(Map<String, Object> parameters) {
            this.task.parameters = parameters;
            return this;
        }

        public TaskDetailBuilder setSchedulingTime(long schedulingTime) {
            this.task.schedulingTime = schedulingTime;
            return this;
        }

        public TaskDetailBuilder setRejectRetries(int rejectRetries) {
            this.task.rejectRetries = rejectRetries;
            return this;
        }

        public TaskDetailBuilder setRejectInterval(int rejectInterval) {
            this.task.rejectInterval = rejectInterval;
            return this;
        }

        public TaskDetail build() {
            return task;
        }
    }

}
