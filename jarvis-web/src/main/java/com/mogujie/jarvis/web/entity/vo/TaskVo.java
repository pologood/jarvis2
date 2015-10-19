package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.Task;

import java.util.Date;

/**
 * Created by hejian on 15/9/17.
 */
public class TaskVo extends Task {

    private String jobType;
    private String jobName;
    private String submitUser;
    private String content;
    private String params;
    private Integer priority;
    private String appName;



    private Date activeStartDate;
    private Date activeEndDate;
    private Integer workerGroupId;

    private Long executeTime;   //执行所用时间
    private Long avgExecuteTime;

    private String scheduleTimeStr;
    private String activeStartDateStr;
    private String activeEndDateStr;
    private String createTimeStr;
    private String updateTimeStr;
    private String executeStartTimeStr;
    private String executeEndTimeStr;

    private String taskStatus;
    private String jobPriority;




    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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

    public String getActiveStartDateStr() {
        return activeStartDateStr;
    }

    public void setActiveStartDateStr(String activeStartDateStr) {
        this.activeStartDateStr = activeStartDateStr;
    }

    public String getActiveEndDateStr() {
        return activeEndDateStr;
    }

    public void setActiveEndDateStr(String activeEndDateStr) {
        this.activeEndDateStr = activeEndDateStr;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getUpdateTimeStr() {
        return updateTimeStr;
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }

    public String getExecuteStartTimeStr() {
        return executeStartTimeStr;
    }

    public void setExecuteStartTimeStr(String executeStartTimeStr) {
        this.executeStartTimeStr = executeStartTimeStr;
    }

    public String getExecuteEndTimeStr() {
        return executeEndTimeStr;
    }

    public void setExecuteEndTimeStr(String executeEndTimeStr) {
        this.executeEndTimeStr = executeEndTimeStr;
    }

    public String getScheduleTimeStr() {
        return scheduleTimeStr;
    }

    public void setScheduleTimeStr(String scheduleTimeStr) {
        this.scheduleTimeStr = scheduleTimeStr;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getJobPriority() {
        return jobPriority;
    }

    public void setJobPriority(String jobPriority) {
        this.jobPriority = jobPriority;
    }

    public Long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Long executeTime) {
        this.executeTime = executeTime;
    }

    public Long getAvgExecuteTime() {
        return avgExecuteTime;
    }

    public void setAvgExecuteTime(Long avgExecuteTime) {
        this.avgExecuteTime = avgExecuteTime;
    }
}
