/*
 * 蘑菇街 Inc. Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年11月25日 下午1:25:20
 */

package com.mogujie.jarvis.server.alarm;

public enum AlarmType {
    SMS(0), TT(1), EMAIL(2), WEBCHAT(3);

    private int type;

    AlarmType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static AlarmType getInstance(int value) {
        AlarmType[] all = AlarmType.values();
        AlarmType select = AlarmType.SMS;
        for (AlarmType s : all) {
            if (s.getType() == value) {
                select = s;
                break;
            }
        }

        return select;
    }
}
