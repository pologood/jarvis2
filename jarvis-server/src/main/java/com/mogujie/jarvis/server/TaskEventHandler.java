/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月20日 上午11:02:58
 */

package com.mogujie.jarvis.server;

import com.lmax.disruptor.WorkHandler;
import com.mogujie.jarvis.server.domain.TaskEvent;

/**
 * 
 *
 */
public class TaskEventHandler implements WorkHandler<TaskEvent> {

    @Override
    public void onEvent(TaskEvent event) throws Exception {

    }

}
