/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月15日 下午4:46:47
 */

package com.mogujie.jarvis.core;

import java.util.Map;

/**
 * 
 *
 */
public class Task {

    private String fullId;
    private String taskName;
    private String appName;
    private String user;
    private String taskType;
    private String command;
    private int priority;
    private int groupId;
    private Map<String, Object> parameters;

    private Task() {
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

    public int getGroupId() {
        return groupId;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public static TaskBuilder newTaskBuilder() {
        return new TaskBuilder();
    }

    public static class TaskBuilder {

        private Task task;

        private TaskBuilder() {
        }

        public TaskBuilder setFullId(String fullId) {
            this.task.fullId = fullId;
            return this;
        }

        public TaskBuilder setTaskName(String taskName) {
            this.task.taskName = taskName;
            return this;
        }

        public TaskBuilder setAppName(String appName) {
            this.task.appName = appName;
            return this;
        }

        public TaskBuilder setUser(String user) {
            this.task.user = user;
            return this;
        }

        public TaskBuilder setTaskType(String taskType) {
            this.task.taskType = taskType;
            return this;
        }

        public TaskBuilder setCommand(String command) {
            this.task.command = command;
            return this;
        }

        public TaskBuilder setPriority(int priority) {
            this.task.priority = priority;
            return this;
        }

        public TaskBuilder setGroupId(int groupId) {
            this.task.groupId = groupId;
            return this;
        }

        public TaskBuilder setParameters(Map<String, Object> parameters) {
            this.task.parameters = parameters;
            return this;
        }

        public Task build() {
            return task;
        }
    }

}
