/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午11:22:11
 */

package com.mogujie.jarvis.server.scheduler.dag.strategy;

/**
 * @author guangming
 *
 */
public interface IOffsetDependStrategy {

    public boolean check(long jobId, int offset, CommonStrategy commonStrategy);
}
