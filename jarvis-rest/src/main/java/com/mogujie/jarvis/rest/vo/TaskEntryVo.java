/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月3日 上午11:53:51
 */

package com.mogujie.jarvis.rest.vo;

import java.util.List;

/**
 * @author guangming
 *
 */
public class TaskEntryVo {
    private String taskName;
    private String user;
    private String taskType;
    private String content;
    private int groupId;
    private Integer priority;
    private Integer rejectRetries;
    private Integer rejectInterval;
    private Integer failedRetries;
    private Integer failedInterval;
    private List<ParameterEntity> params;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getRejectRetries() {
        return rejectRetries;
    }

    public void setRejectRetries(Integer rejectRetries) {
        this.rejectRetries = rejectRetries;
    }

    public Integer getRejectInterval() {
        return rejectInterval;
    }

    public void setRejectInterval(Integer rejectInterval) {
        this.rejectInterval = rejectInterval;
    }

    public Integer getFailedRetries() {
        return failedRetries;
    }

    public void setFailedRetries(Integer failedRetries) {
        this.failedRetries = failedRetries;
    }

    public Integer getFailedInterval() {
        return failedInterval;
    }

    public void setFailedInterval(Integer failedInterval) {
        this.failedInterval = failedInterval;
    }

    public List<ParameterEntity> getParams() {
        return params;
    }

    public void setParams(List<ParameterEntity> params) {
        this.params = params;
    }

    public Integer getPriority(Integer defaultValue) {
        return (priority != null) ? priority : defaultValue;
    }

    public Integer getRejectRetries(Integer defaultValue) {
        return (rejectRetries != null) ? rejectRetries : defaultValue;
    }

    public Integer getRejectInterval(Integer defaultValue) {
        return (rejectInterval != null) ? rejectInterval : defaultValue;
    }

    public Integer getFailedRetries(Integer defaultValue) {
        return (failedRetries != null) ? failedRetries : defaultValue;
    }

    public Integer getFailedInterval(Integer defaultValue) {
        return (failedInterval != null) ? failedInterval : defaultValue;
    }

}
