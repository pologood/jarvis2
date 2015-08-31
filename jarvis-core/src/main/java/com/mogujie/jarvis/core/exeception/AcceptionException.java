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
public class AcceptionException extends Exception {

    private static final long serialVersionUID = 1L;

    private String message;

    public AcceptionException() {
        super();
    }

    public AcceptionException(final String message) {
        super(message);
    }

    public AcceptionException(final Exception e) {
        super(e);
    }

    public AcceptionException(Throwable cause) {
        super(cause);
    }

    public AcceptionException(final String message, final Throwable cause) {
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
