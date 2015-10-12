/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月30日 上午11:35:47
 */

package com.mogujie.jarvis.server.domain;

/**
 * @author guangming
 *
 */
public class ModifyJobEntry {
    private ModifyOperation operation;
    private Object value;

    public ModifyJobEntry(ModifyOperation operation, Object newValue) {
        this.operation = operation;
        this.value = newValue;
    }

    public ModifyOperation getOperation() {
        return operation;
    }

    public void setOperation(ModifyOperation operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
