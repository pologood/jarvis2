package com.mogujie.jarvis.web.entity.qo;

import com.mogujie.jarvis.dto.generate.Worker;

/**
 * Created by hejian on 15/9/28.
 */
public class WorkerQo extends Worker {
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
