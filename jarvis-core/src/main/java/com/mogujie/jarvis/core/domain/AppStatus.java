/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming
 * Create Date: 2015年9月29日 上午11:10:39
 */

package com.mogujie.jarvis.core.domain;

/**
 * 
 *
 */
public enum AppStatus {

    DISABLED(0),    //无效
    ENABLE(1);      //有效

    private  int value;

    AppStatus(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Boolean isValid(int value) {
        AppStatus[] values = AppStatus.values();
        for (AppStatus s : values) {
            if (s.getValue() == value) {
                return true;
            }
        }
        return false;
    }

}
