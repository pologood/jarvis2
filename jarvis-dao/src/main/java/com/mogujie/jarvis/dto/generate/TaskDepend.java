package com.mogujie.jarvis.dto.generate;

import java.io.Serializable;
import java.util.Date;

public class TaskDepend implements Serializable {
    private Long taskId;

    private String dependTaskIds;

    private String childTaskIds;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getDependTaskIds() {
        return dependTaskIds;
    }

    public void setDependTaskIds(String dependTaskIds) {
        this.dependTaskIds = dependTaskIds;
    }

    public String getChildTaskIds() {
        return childTaskIds;
    }

    public void setChildTaskIds(String childTaskIds) {
        this.childTaskIds = childTaskIds;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}