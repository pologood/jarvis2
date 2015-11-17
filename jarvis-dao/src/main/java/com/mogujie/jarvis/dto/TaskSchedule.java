package com.mogujie.jarvis.dto;

import java.io.Serializable;
import java.util.Date;

public class TaskSchedule extends TaskScheduleKey implements Serializable {
    private Long scheduleTime;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}