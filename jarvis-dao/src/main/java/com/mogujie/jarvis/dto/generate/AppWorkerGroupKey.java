package com.mogujie.jarvis.dto.generate;

import java.io.Serializable;

public class AppWorkerGroupKey implements Serializable {
    private Integer appId;

    private Integer workerGroupId;

    private static final long serialVersionUID = 1L;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getWorkerGroupId() {
        return workerGroupId;
    }

    public void setWorkerGroupId(Integer workerGroupId) {
        this.workerGroupId = workerGroupId;
    }
}