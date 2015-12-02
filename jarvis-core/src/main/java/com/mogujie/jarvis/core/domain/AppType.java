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
public enum AppType {

    NORMAL(1),    //普通
    ADMIN(2);      //管理

    private  int value;

    AppType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
