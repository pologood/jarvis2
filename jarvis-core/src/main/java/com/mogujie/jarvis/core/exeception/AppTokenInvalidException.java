/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午2:52:35
 */

package com.mogujie.jarvis.core.exeception;

/**
 * @author wuya
 *
 */
public class AppTokenInvalidException extends Exception {

    private static final long serialVersionUID = 1L;

    private String message;

    public AppTokenInvalidException() {
        super();
    }

    public AppTokenInvalidException(final String message) {
        super(message);
    }

    public AppTokenInvalidException(final Exception e) {
        super(e);
    }

    public AppTokenInvalidException(Throwable cause) {
        super(cause);
    }

    public AppTokenInvalidException(final String message, final Throwable cause) {
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
