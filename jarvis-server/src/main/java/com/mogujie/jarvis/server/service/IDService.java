/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月24日 下午8:13:57
 */

package com.mogujie.jarvis.server.service;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.mogujie.jarvis.dao.diy.IDMapper;

/**
 *
 *
 */
//@Service //测试有问题，先注释掉
public class IDService {

    @Autowired
    private IDMapper idMapper;

    private AtomicLong atomicJobId;
    private AtomicLong atomicTaskId;

    @PostConstruct
    public void init() {
        Long jobId = idMapper.selectMaxJobId();
        atomicJobId = jobId == null ? new AtomicLong(0) : new AtomicLong(jobId);

        Long taskId = idMapper.selectMaxTaskId();
        atomicTaskId = taskId == null ? new AtomicLong(0) : new AtomicLong(taskId);
    }

    public long getNextJobId() {
        return atomicJobId.incrementAndGet();
    }

    public long getNextTaskId() {
        return atomicTaskId.incrementAndGet();
    }

}
