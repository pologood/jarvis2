/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月5日 下午3:30:47
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.List;



/**
 * @author guangming
 *
 */
public class OffsetTaskSchedule extends AbstractTaskSchedule {

    @Override
    public boolean check(long scheduleTime) {
        return true;
    }

    @Override
    public List<ScheduleTask> getSchedulingTasks() {
        return null;
    }

    @Override
    public List<ScheduleTask> getSelectedTasks() {
        return null;
    }

}
