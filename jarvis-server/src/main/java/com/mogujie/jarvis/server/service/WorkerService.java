/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月15日 下午2:42:26
 */
package com.mogujie.jarvis.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.dao.WorkerMapper;
import com.mogujie.jarvis.dto.Worker;

/**
 * @author wuya
 *
 */
@Service
public class WorkerService {

    @Autowired
    private WorkerMapper workerMapper;

    public Worker queryWorker(String ip, int port) {
        // TODO
        return null;
    }

    public int updateClientStatus(int id, int status) {
        // TODO
        return 0;
    }
}
