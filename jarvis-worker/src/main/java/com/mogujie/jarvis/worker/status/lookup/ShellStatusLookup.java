/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2016年1月6日 下午5:06:51
 */

package com.mogujie.jarvis.worker.status.lookup;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.worker.status.TaskStatusLookup;

public class ShellStatusLookup implements TaskStatusLookup {

    @Override
    public void init(Configuration conf) {

    }

    @Override
    public int lookup(TaskDetail taskDetail) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
