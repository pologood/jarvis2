package com.mogujie.jarvis.core.domain;

/**
 * Created by hejian on 16/1/8.
 */
public enum WorkerGroupStatus {
    ENABLE(1,"启用"),
    DISABLED(2,"禁用");

    int value;
    String description;

    WorkerGroupStatus(int value,String description){
        this.value=value;
        this.description=description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static Boolean isValid(int value) {
        WorkerGroupStatus[] values = WorkerGroupStatus.values();
        for (WorkerGroupStatus s : values) {
            if (s.getValue() == value) {
                return true;
            }
        }
        return false;
    }
}
