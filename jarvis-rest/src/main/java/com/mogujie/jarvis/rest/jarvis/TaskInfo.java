/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月23日 下午1:16:31
 */

package com.mogujie.jarvis.rest.jarvis;

import java.io.Serializable;

public class TaskInfo implements Serializable {

    private static final long serialVersionUID = 6129998102397429730L;
    public static final String TARGETMETHOD = "run";

    private Integer id;

    public boolean isCritical() {
        return isCritical;
    }

    public void setCritical(boolean isCritical) {
        this.isCritical = isCritical;
    }

    private boolean isCritical;
    private String cronExp;
    private String cronExpExplain;
    private Integer scriptId;
    private String title;
    private String file;
    private Integer priority;
    private String publisher;
    private String receiver;
    private String publishTime;
    private String editTime;
    private String startDate;
    private String endDate;
    private Integer status;
    private String dayTask;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPline() {
        return pline;
    }

    public void setPline(String pline) {
        this.pline = pline;
    }

    //脚本所属部门
    private String department;
    //脚本所属生产线
    private String pline;

    // 前置程序IDS
    private String preTaskIds;

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    private boolean isDeleted;

    public TaskInfo(String username, Integer scriptId, String title,
                String cronExp, String startDate, String endDate, String receiver) {
        this.publisher = username;
        this.scriptId = scriptId;
        this.title = title;
        this.cronExp = cronExp;
        this.startDate = startDate;
        this.endDate = endDate;
        this.receiver = receiver;
        this.status = new Integer(1);
        this.priority = TaskPriorityEnum.MID.getValue();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public String getCronExpExplain() {
        return cronExpExplain;
    }

    public void setCronExpExplain(String cronExpExplain) {
        this.cronExpExplain = cronExpExplain;
    }

    public Integer getScriptId() {
        return scriptId;
    }

    public void setScriptId(Integer scriptId) {
        this.scriptId = scriptId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Integer getPriority() {
        return priority;
    }

    public String fetchPriorityDisplay() {
        return TaskPriorityEnum.get(getPriority()).getDescription();
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getEditTime() {
        return editTime;
    }

    public void setEditTime(String editTime) {
        this.editTime = editTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getPreTaskIds() {
        return preTaskIds;
    }

    public void setPreTaskIds(String preTaskIds) {
        this.preTaskIds = preTaskIds;
    }

    public String getDayTask() {
        return dayTask;
    }

    public void setDayTask(String dayTask) {
        this.dayTask = dayTask;
    }

    public String getPriorityDisplay() {
        return TaskPriorityEnum.get(getPriority()).getDescription();
    }

    public String toString() {

        return " crontab:"+cronExp+" scriptId:"+scriptId
                +" title:"+title+" priority:"+priority+" receiver:"
                +receiver+" publisher:"+publisher+" status:"+status;
    }
}
