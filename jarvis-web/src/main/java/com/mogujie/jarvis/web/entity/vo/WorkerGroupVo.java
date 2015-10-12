package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.WorkerGroup;

/**
 * Created by hejian on 15/9/28.
 */
public class WorkerGroupVo extends WorkerGroup {
    private String createTimeStr;
    private String updateTimeStr;

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
