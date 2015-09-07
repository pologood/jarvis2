/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午2:52:35
 */

package com.mogujie.jarvis.core.exeception;

/**
 * @author muming
 *
 */
public class ShellException extends Exception {

    private static final long serialVersionUID = 1L;

    private String message;

    public ShellException() {
        super();
    }

    public ShellException(final String message) {
        super(message);
    }

    public ShellException(final Exception e) {
        super(e);
    }

    public ShellException(Throwable cause) {
        super(cause);
    }

    public ShellException(final String message, final Throwable cause) {
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
