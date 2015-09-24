/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午2:04:58
 */

package com.mogujie.jarvis.server.scheduler;

import com.mogujie.jarvis.core.exeception.TaskException;

/**
 * @author guangming
 *
 */
public class JobScheduleException extends TaskException {

    private static final long serialVersionUID = 1L;

    private String message;

    public JobScheduleException() {
        super();
    }

    public JobScheduleException(final String message) {
        super(message);
    }

    public JobScheduleException(final Exception e) {
        super(e);
    }

    public JobScheduleException(Throwable cause) {
        super(cause);
    }

    public JobScheduleException(final String message, final Throwable cause) {
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