/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2016年1月6日 下午4:34:26
 */

package com.mogujie.jarvis.worker.status;

import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.domain.TaskDetail;

import akka.actor.Status;

public interface TaskStateStore {

    void init(Configuration conf);

    void write(TaskDetail taskDetail, int status);

    Map<TaskDetail, Status> restore();

    void close();
}
