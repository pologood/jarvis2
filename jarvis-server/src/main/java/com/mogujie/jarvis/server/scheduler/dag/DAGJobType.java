/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月28日 下午4:31:12
 */

package com.mogujie.jarvis.server.scheduler.dag;

/**
 * @author guangming
 *
 */
public enum DAGJobType {
    NONE("---"),
    TIME("--t"),          // 定时任务
    DEPEND("-d-"),        // 依赖任务
    DEPEND_TIME("-dt"),
    CYCLE("c--"),         // 固定延迟任务
    CYCLE_TIME("c-t"),
    CYCLE_DEPEND("cd-"),
    ALL("cdt");

    private static final DAGJobType[] VALS = values();

    public final String value;

    private DAGJobType(String value) {
        this.value = value;
    }

    /**
     * return true if this type implies that type
     * @param that
     */
    public boolean implies(DAGJobType that) {
        if (that != null) {
            return (ordinal() & that.ordinal()) == that.ordinal();
        }
        return false;
    }

    /** AND operation. */
    public DAGJobType and(DAGJobType that) {
        return VALS[ordinal() & that.ordinal()];
    }

    /** OR operation. */
    public DAGJobType or(DAGJobType that) {
        return VALS[ordinal() | that.ordinal()];
    }

    /** NOT operation. */
    public DAGJobType not() {
        return VALS[7 - ordinal()];
    }

    /**
     * remove that type from this
     */
    public DAGJobType remove(DAGJobType that) {
        return and(that.not());
    }
}
