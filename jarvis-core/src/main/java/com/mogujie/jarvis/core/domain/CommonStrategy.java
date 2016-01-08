package com.mogujie.jarvis.core.domain;

/**
 * Created by hejian on 16/1/8.
 */
public enum CommonStrategy {
    ALL(1,"全部成功"),
    LASTONE(2,"最后一次成功"),
    ANYONE(3,"任何一次成功");

    private int value;
    private String description;

    CommonStrategy(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static Boolean isValid(int value) {
        CommonStrategy[] values = CommonStrategy.values();
        for (CommonStrategy s : values) {
            if (s.getValue() == value) {
                return true;
            }
        }
        return false;
    }
}
