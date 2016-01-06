/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2016年1月6日 下午4:57:59
 */

package com.mogujie.jarvis.worker.status.store;

import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.worker.status.TaskStateStore;

import akka.actor.Status;

public class LocalFileSystemStateStore implements TaskStateStore {

    @Override
    public void init(Configuration conf) {
        // TODO Auto-generated method stub

    }

    @Override
    public void write(TaskDetail taskDetail, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<TaskDetail, Status> restore() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
