/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午2:06:11
 */

package com.mogujie.jarvis.server.scheduler.dag;

import com.mogujie.jarvis.server.scheduler.JobScheduleException;

/**
 * @author guangming
 *
 */
public class DAGScheduleException extends JobScheduleException {
    private static final long serialVersionUID = 1L;

    private String message;

    public DAGScheduleException() {
        super();
    }

    public DAGScheduleException(final String message) {
        super(message);
    }

    public DAGScheduleException(final Exception e) {
        super(e);
    }

    public DAGScheduleException(Throwable cause) {
        super(cause);
    }

    public DAGScheduleException(final String message, final Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return this.message == null ? super.getMessage() : this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
