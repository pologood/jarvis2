/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:10:33
 */

package com.mogujie.jarvis.server.scheduler.DAG.status;

/**
 * @author guangming
 *
 */
public enum JobDependencyStrategy {
    ANYONE,     // 依赖任何一次成功
    LASTONE,    // 依赖最后一次成功
    ALL;        // 依赖全部成功
}
