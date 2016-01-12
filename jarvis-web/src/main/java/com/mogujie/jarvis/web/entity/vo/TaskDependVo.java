package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.generate.TaskDepend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hejian on 15/12/8.
 */
public class TaskDependVo extends TaskDepend {

    private Integer status;
    private Date scheduleTime;
    private Date executeStartTime;
    private Date executeEndTime;
    private Long executeTime;
    private String executeUser;
    private List<TaskDependVo> children = new ArrayList<TaskDependVo>();
    private List<TaskDependVo> parents = new ArrayList<TaskDependVo>();

    private boolean parentFlag = false;
    private boolean rootFlag = false;

    private Long jobId;
    private String jobName;
    private Integer completeTask;
    private Integer totalTask;
    private List<TaskVo> taskList = new ArrayList<TaskVo>();
    private List<TaskHistoryVo> taskExecuteRecordsVoList;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public Date getExecuteStartTime() {
        return executeStartTime;
    }

    public void setExecuteStartTime(Date executeStartTime) {
        this.executeStartTime = executeStartTime;
    }

    public Date getExecuteEndTime() {
        return executeEndTime;
    }

    public void setExecuteEndTime(Date executeEndTime) {
        this.executeEndTime = executeEndTime;
    }

    public Long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Long executeTime) {
        this.executeTime = executeTime;
    }

    public List<TaskDependVo> getChildren() {
        return children;
    }

    public void setChildren(List<TaskDependVo> children) {
        this.children = children;
    }

    public List<TaskDependVo> getParents() {
        return parents;
    }

    public void setParents(List<TaskDependVo> parents) {
        this.parents = parents;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getCompleteTask() {
        return completeTask;
    }

    public void setCompleteTask(Integer completeTask) {
        this.completeTask = completeTask;
    }

    public Integer getTotalTask() {
        return totalTask;
    }

    public void setTotalTask(Integer totalTask) {
        this.totalTask = totalTask;
    }

    public List<TaskVo> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskVo> taskList) {
        this.taskList = taskList;
    }

    public boolean isParentFlag() {
        return parentFlag;
    }

    public void setParentFlag(boolean parentFlag) {
        this.parentFlag = parentFlag;
    }

    public boolean isRootFlag() {
        return rootFlag;
    }

    public void setRootFlag(boolean rootFlag) {
        this.rootFlag = rootFlag;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public List<TaskHistoryVo> getTaskExecuteRecordsVoList() {
        return taskExecuteRecordsVoList;
    }

    public void setTaskExecuteRecordsVoList(List<TaskHistoryVo> taskExecuteRecordsVoList) {
        this.taskExecuteRecordsVoList = taskExecuteRecordsVoList;
    }
}