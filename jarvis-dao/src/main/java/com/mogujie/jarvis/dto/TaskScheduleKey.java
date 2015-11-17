package com.mogujie.jarvis.dto;

import java.io.Serializable;

public class TaskScheduleKey implements Serializable {
    private Long jobId;

    private Long preJobId;

    private Long preTaskId;

    private static final long serialVersionUID = 1L;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getPreJobId() {
        return preJobId;
    }

    public void setPreJobId(Long preJobId) {
        this.preJobId = preJobId;
    }

    public Long getPreTaskId() {
        return preTaskId;
    }

    public void setPreTaskId(Long preTaskId) {
        this.preTaskId = preTaskId;
    }
}