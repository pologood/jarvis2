package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.generate.Task;

import java.util.List;

/**
 * Created by hejian on 15/9/17.
 */
public class TaskQo extends Task {
    private String scheduleDate;
    private String executeDate;
    private String startDate;
    private String endDate;
    private String jobName;
    private String jobType;
    private String submitUser;
    private String taskStatusArrStr;
    private List<Integer> taskStatus;
    private String order;
    private Integer offset;
    private Integer limit;
    private String orderField;



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

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public String getExecuteDate() {
        return executeDate;
    }

    public void setExecuteDate(String executeDate) {
        this.executeDate = executeDate;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
