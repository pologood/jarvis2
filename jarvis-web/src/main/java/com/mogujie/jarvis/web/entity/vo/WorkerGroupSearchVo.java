package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.WorkerGroup;

/**
 * Created by hejian on 15/9/28.
 */
public class WorkerGroupSearchVo extends WorkerGroup {
    private Integer offset;
    private Integer limit;

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
