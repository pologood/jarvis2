/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月31日 下午3:51:19
 */

package com.mogujie.jarvis.core.domain;

/**
 * @author guangming
 *
 */
public enum TaskType {

    SCHEDULE(1), //调度系统自动调度的task
    RERUN(2),    //手动重跑的task
    TEMP(3);     //一次性的临时task

    private int value;

    TaskType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TaskType parseValue(int value) {
        TaskType[] typeList = TaskType.values();
        for (TaskType s : typeList) {
            if (s.getValue() == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("TaskType value is invalid. value:" + value);
    }

}
