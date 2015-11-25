package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.generate.App;

/**
 * Created by hejian on 15/9/24.
 */
public class AppVo extends App {
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
