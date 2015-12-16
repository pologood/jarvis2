package com.mogujie.jarvis.web.entity.qo;

import com.mogujie.jarvis.dto.generate.App;

/**
 * Created by hejian on 15/9/24.
 */
public class AppQo extends App {
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
