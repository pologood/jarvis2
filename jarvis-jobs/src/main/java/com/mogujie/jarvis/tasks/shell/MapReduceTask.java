/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月28日 下午4:57:49
 */

package com.mogujie.jarvis.tasks.shell;

import java.util.List;

import com.google.common.base.Joiner;
import com.mogujie.jarvis.worker.TaskContext;

public class MapReduceTask extends JavaTask {

    public MapReduceTask(TaskContext taskContext) {
        super(taskContext);
    }

    @Override
    protected String getCmd(String jar, List<String> classpath, String mainClass, String args) {
        return "yarn jar " + jar + " -libjars " + Joiner.on(",").join(classpath) + " " + mainClass + " " + args;
    }

}
