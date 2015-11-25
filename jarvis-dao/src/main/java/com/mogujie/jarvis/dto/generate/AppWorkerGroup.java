package com.mogujie.jarvis.dto.generate;

import java.io.Serializable;
import java.util.Date;

public class AppWorkerGroup extends AppWorkerGroupKey implements Serializable {
    private Date createTime;

    private Date updateTime;

    private String updateUser;

    private static final long serialVersionUID = 1L;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}