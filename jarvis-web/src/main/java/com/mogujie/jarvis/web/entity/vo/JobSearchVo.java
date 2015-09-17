package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.Job;

/**
 * Created by hejian on 15/9/15.
 */
public class JobSearchVo extends Job {
    private Integer offset;
    private Integer limit;
    private String order;

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

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
