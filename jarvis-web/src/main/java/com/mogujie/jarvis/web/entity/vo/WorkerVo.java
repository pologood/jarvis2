package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.Worker;

/**
 * Created by hejian on 15/9/28.
 */
public class WorkerVo extends Worker {
    private String statusStr;
    private String createTimeStr;
    private String updateTimeStr;

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
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
}
