/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月10日 下午3:50:06
 */
package com.mogujie.jarvis.core.domain;

/**
 * @author wuya
 */
public enum StreamType {
    STD_OUT(1), STD_ERR(2);

    private int value;

    StreamType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static StreamType parseValue(int value) {
        StreamType[] all = StreamType.values();
        for (StreamType s : all) {
            if (s.getValue() == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("StreamType value is invalid. value:" + value);
    }

}
