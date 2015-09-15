/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午2:54:51
 */

package com.mogujie.jarvis.core;

import java.util.Map;

/**
 * @author wuya
 *
 */
public class TaskContext {

    private String fullId;
    private String taskName;
    private String appName;
    private String user;
    private String taskType;
    private String command;
    private int priority;
    private Map<String, Object> parameters;

    private AbstractLogCollector logCollector;
    private ProgressReporter progressReporter;

    private TaskContext() {
    }

    public static TaskContextBuilder newBuilder() {
        return new TaskContextBuilder();
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

    public String getCommand() {
        return command;
    }

    public int getPriority() {
        return priority;
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

    public static class TaskContextBuilder {

        private TaskContext taskContext;

        private TaskContextBuilder() {
        }

        public TaskContextBuilder setFullId(String fullId) {
            this.taskContext.fullId = fullId;
            return this;
        }

        public TaskContextBuilder setTaskName(String taskName) {
            this.taskContext.taskName = taskName;
            return this;
        }

        public TaskContextBuilder setAppName(String appName) {
            this.taskContext.appName = appName;
            return this;
        }

        public TaskContextBuilder setUser(String user) {
            this.taskContext.user = user;
            return this;
        }

        public TaskContextBuilder setTaskType(String taskType) {
            this.taskContext.taskType = taskType;
            return this;
        }

        public TaskContextBuilder setCommand(String command) {
            this.taskContext.command = command;
            return this;
        }

        public TaskContextBuilder setPriority(int priority) {
            this.taskContext.priority = priority;
            return this;
        }

        public TaskContextBuilder setParameters(Map<String, Object> parameters) {
            this.taskContext.parameters = parameters;
            return this;
        }

        public TaskContextBuilder setLogCollector(AbstractLogCollector logCollector) {
            this.taskContext.logCollector = logCollector;
            return this;
        }

        public TaskContextBuilder setProgressReporter(ProgressReporter progressReporter) {
            this.taskContext.progressReporter = progressReporter;
            return this;
        }

        public TaskContext build() {
            return this.taskContext;
        }

    }

}
