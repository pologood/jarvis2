/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2016年1月6日 下午2:06:51
 */

package com.mogujie.jarvis.core.metrics;

public interface MetricsSink {

    void putMetrics(MetricsRecord metricsRecord);

    void flush();
}
