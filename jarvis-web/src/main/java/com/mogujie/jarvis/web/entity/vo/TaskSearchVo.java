package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.Task;

import java.util.List;

/**
 * Created by hejian on 15/9/17.
 */
public class TaskSearchVo extends Task {
    private String executeDate;
    private String dataDate;
    private String startTime;
    private String endTime;
    private String jobName;
    private String jobType;
    private String submitUser;
    private String taskStatusArrStr;
    private List<Integer> taskStatus;
    private String order;
    private Integer offset;
    private Integer limit;

    public String getExecuteDate() {
        return executeDate;
    }

    public void setExecuteDate(String executeDate) {
        this.executeDate = executeDate;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
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

    public String getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
    }

    public String getTaskStatusArrStr() {
        return taskStatusArrStr;
    }

    public void setTaskStatusArrStr(String taskStatusArrStr) {
        this.taskStatusArrStr = taskStatusArrStr;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<Integer> getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(List<Integer> taskStatus) {
        this.taskStatus = taskStatus;
    }

}
