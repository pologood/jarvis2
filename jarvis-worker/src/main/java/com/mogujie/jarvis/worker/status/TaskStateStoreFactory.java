/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2016年1月13日 下午2:14:16
 */

package com.mogujie.jarvis.worker.status;

import org.apache.commons.configuration.Configuration;

import com.google.common.base.Throwables;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.worker.WorkerConfigKeys;

public class TaskStateStoreFactory {

    private static Configuration config = ConfigUtils.getWorkerConfig();
    private static TaskStateStore taskStateStore = null;

    static {
        try {
            taskStateStore = (TaskStateStore) Class.forName(config.getString(WorkerConfigKeys.WORKER_TASK_STATE_STORE_CLASS)).newInstance();
            taskStateStore.init(config);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Throwables.propagate(e);
        }
    }

    public static TaskStateStore getInstance() {
        return taskStateStore;
    }

}
