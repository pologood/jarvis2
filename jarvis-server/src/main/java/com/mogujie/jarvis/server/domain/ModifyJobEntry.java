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
    private MODIFY_OPERATION operation;
    private Object value;

    public ModifyJobEntry(MODIFY_OPERATION operation, Object newValue) {
        this.operation = operation;
        this.value = newValue;
    }

    public MODIFY_OPERATION getOperation() {
        return operation;
    }

    public void setOperation(MODIFY_OPERATION operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
