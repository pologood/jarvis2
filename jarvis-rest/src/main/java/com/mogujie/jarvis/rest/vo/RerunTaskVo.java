/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月3日 上午11:34:33
 */

package com.mogujie.jarvis.rest.vo;

import java.util.List;

/**
 * @author guangming
 *
 */
public class RerunTaskVo extends  AbstractVo {
    private List<Long> jobIdList;
    private long startDate;
    private long endDate;
    private boolean runChild;

    public List<Long> getJobIdList() {
        return jobIdList;
    }
    public void setJobIdList(List<Long> jobIdList) {
        this.jobIdList = jobIdList;
    }
    public long getStartDate() {
        return startDate;
    }
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }
    public long getEndDate() {
        return endDate;
    }
    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
    public boolean isRunChild() {
        return runChild;
    }
    public void setRunChild(boolean runChild) {
        this.runChild = runChild;
    }
}
