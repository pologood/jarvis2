/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月23日 下午1:15:59
 */

package com.mogujie.jarvis.rest.jarvis;

import java.util.List;

public class TasksResult extends Result {

    private static final long serialVersionUID = 291430869192344650L;
    private List<TaskInfo> tasks;

    public List<TaskInfo> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskInfo> tasks) {
        this.tasks = tasks;
    }

}

