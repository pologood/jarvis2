package com.mogujie.jarvis.rest.vo;

import java.util.List;

/**
 * AppWorkerGroupVo
 *
 * @author muming
 */


public class AppWorkerGroupVo extends AbstractVo{

    public class AppWorkerGroupEntry {
        private Integer appId;
        private Integer workGroupId;

        public Integer getAppId() {
            return appId;
        }

        public void setAppId(Integer appId) {
            this.appId = appId;
        }

        public Integer getWorkGroupId() {
            return workGroupId;
        }

        public void setWorkGroupId(Integer workGroupId) {
            this.workGroupId = workGroupId;
        }
    }

    private List<AppWorkerGroupEntry> list;

    public List<AppWorkerGroupEntry> getList() {
        return list;
    }

    public void setList(List<AppWorkerGroupEntry> list) {
        this.list = list;
    }
}
