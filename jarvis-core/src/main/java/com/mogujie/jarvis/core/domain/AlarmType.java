package com.mogujie.jarvis.core.domain;

/**
 * Created by hejian on 16/1/8.
 */
public enum AlarmType {

    SMS(1, "短信"),      //短信
    TT(2, "TT"),        //TT
    EMAIL(3, "邮件"),    //邮件
    WEIXIN(4, "微信");   //微信

    private int value;
    private String description;

    AlarmType(int value, String description) {
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
        AlarmType[] values = AlarmType.values();
        for (AlarmType s : values) {
            if (s.getValue() == value) {
                return true;
            }
        }
        return false;
    }
}
