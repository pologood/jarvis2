/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午2:52:35
 */

package com.mogujie.jarvis.core.metrics;

public class MetricsException extends Exception {

    private static final long serialVersionUID = 1L;

    public MetricsException() {
        super();
    }

    public MetricsException(final String message) {
        super(message);
    }

    public MetricsException(Throwable cause) {
        super(cause);
    }

    public MetricsException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
