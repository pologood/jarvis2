/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月18日 下午5:12:39
 */

package com.mogujie.jarvis.server.scheduler.event;

import java.util.List;

import com.mogujie.jarvis.core.observer.Event;

/**
 * @author guangming
 *
 */
public class RetryTaskEvent implements Event {

    private List<Long> taskIdList;
    private boolean runChild;

    /**
     * @param taskIdList
     * @param runChild
     */
    public RetryTaskEvent(List<Long> taskIdList, boolean runChild) {
        this.taskIdList = taskIdList;
        this.runChild = runChild;
    }

    public List<Long> getTaskIdList() {
        return taskIdList;
    }

    public void setTaskIdList(List<Long> taskIdList) {
        this.taskIdList = taskIdList;
    }

    public boolean isRunChild() {
        return runChild;
    }

    public void setRunChild(boolean runChild) {
        this.runChild = runChild;
    }
}
