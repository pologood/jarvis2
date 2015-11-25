package com.mogujie.jarvis.dto.generate;

import java.io.Serializable;

public class TaskDependKey implements Serializable {
    private Long taskId;

    private Long preJobId;

    private Long preTaskId;

    private static final long serialVersionUID = 1L;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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