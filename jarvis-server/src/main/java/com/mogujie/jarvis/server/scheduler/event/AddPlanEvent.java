/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月31日 上午10:57:13
 */

package com.mogujie.jarvis.server.scheduler.event;

import java.util.List;
import java.util.Map;

/**
 * @author guangming
 *
 */
public class AddPlanEvent extends DAGJobEvent {
    private long scheduleTime;
    private Map<Long, List<Long>> dependTaskIdMap;

    /**
     * @param jobId
     * @param scheduleTime
     * @param dependTaskIdMap
     */
    public AddPlanEvent(long jobId, long scheduleTime, Map<Long, List<Long>> dependTaskIdMap) {
        super(jobId);
        this.scheduleTime = scheduleTime;
        this.dependTaskIdMap = dependTaskIdMap;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public Map<Long, List<Long>> getDependTaskIdMap() {
        return dependTaskIdMap;
    }

    public void setDependTaskIdMap(Map<Long, List<Long>> dependTaskIdMap) {
        this.dependTaskIdMap = dependTaskIdMap;
    }

}
