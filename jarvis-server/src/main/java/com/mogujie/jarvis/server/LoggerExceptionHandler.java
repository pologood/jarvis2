/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2014 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2014年10月28日 下午9:19:29
 */
package com.mogujie.jarvis.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;
import com.lmax.disruptor.ExceptionHandler;

/**
 * @author wuya
 *
 */
public class LoggerExceptionHandler implements ExceptionHandler<Object> {

    private static final Logger LOGGER = LogManager.getLogger();

    public void handleEventException(Throwable t, long sequence, Object event) {
        LOGGER.error(Throwables.getStackTraceAsString(t));
    }

    public void handleOnShutdownException(Throwable t) {
        LOGGER.error(Throwables.getStackTraceAsString(t));
    }

    public void handleOnStartException(Throwable t) {
        LOGGER.error(Throwables.getStackTraceAsString(t));
    }

}
