package com.mogujie.jarvis.dto.generate;

import java.io.Serializable;
import java.util.Date;

public class Job implements Serializable {
    private Long jobId;

    private Long originJobId;

    private String jobName;

    private String jobType;

    private Integer jobFlag;

    private String content;

    private String params;

    private String submitUser;

    private Integer priority;

    private Integer appId;

    private Date activeStartDate;

    private Date activeEndDate;

    private Integer workerGroupId;

    private Integer rejectAttempts;

    private Integer rejectInterval;

    private Integer failedAttempts;

    private Integer failedInterval;

    private Date createTime;

    private Date updateTime;

    private String updateUser;

    private static final long serialVersionUID = 1L;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getOriginJobId() {
        return originJobId;
    }

    public void setOriginJobId(Long originJobId) {
        this.originJobId = originJobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Integer getJobFlag() {
        return jobFlag;
    }

    public void setJobFlag(Integer jobFlag) {
        this.jobFlag = jobFlag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Date getActiveStartDate() {
        return activeStartDate;
    }

    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    public Date getActiveEndDate() {
        return activeEndDate;
    }

    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    public Integer getWorkerGroupId() {
        return workerGroupId;
    }

    public void setWorkerGroupId(Integer workerGroupId) {
        this.workerGroupId = workerGroupId;
    }

    public Integer getRejectAttempts() {
        return rejectAttempts;
    }

    public void setRejectAttempts(Integer rejectAttempts) {
        this.rejectAttempts = rejectAttempts;
    }

    public Integer getRejectInterval() {
        return rejectInterval;
    }

    public void setRejectInterval(Integer rejectInterval) {
        this.rejectInterval = rejectInterval;
    }

    public Integer getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Integer getFailedInterval() {
        return failedInterval;
    }

    public void setFailedInterval(Integer failedInterval) {
        this.failedInterval = failedInterval;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}