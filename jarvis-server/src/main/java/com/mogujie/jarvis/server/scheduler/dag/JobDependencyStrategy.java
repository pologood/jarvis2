/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:10:33
 */

package com.mogujie.jarvis.server.scheduler.dag;

/**
 * @author guangming
 *
 */
public enum JobDependencyStrategy {
    ANYONE(0),     // 依赖任何一次成功
    LASTONE(1),    // 依赖最后一次成功
    ALL(2);        // 依赖全部成功

    private int value;

    JobDependencyStrategy(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
