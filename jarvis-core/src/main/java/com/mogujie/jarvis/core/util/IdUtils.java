/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月29日 上午11:09:46
 */

package com.mogujie.jarvis.core.util;

import com.mogujie.jarvis.core.domain.IdType;

/**
 *
 *
 */
public class IdUtils {

    public static long parse(String fullId, IdType type) {
        String[] tokens = fullId.split("_", 4);
        switch (type) {
            case JOB_ID:
                return Long.parseLong(tokens[0]);
            case JOB_VERSION:
                return Long.parseLong(tokens[1]);
            case TASK_ID:
                return Long.parseLong(tokens[2]);
            case ATTEMPT_ID:
            default:
                return Long.parseLong(tokens[3]);
        }
    }
}
