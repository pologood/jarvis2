/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2016年1月6日 下午2:05:41
 */

package com.mogujie.jarvis.core.metrics;

public interface MetricsRecord {

    long getTimestamp();

    String getName();

    String getDescription();

    String getContext();
}
