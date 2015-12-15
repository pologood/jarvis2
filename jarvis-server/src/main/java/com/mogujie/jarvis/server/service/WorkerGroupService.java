package com.mogujie.jarvis.server.service;

import com.mogujie.jarvis.dao.generate.WorkerGroupMapper;
import com.mogujie.jarvis.dto.generate.WorkerGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author muming
 */
@Service
public class WorkerGroupService {

    @Autowired
    private WorkerGroupMapper workerGroupMapper;

    public int update(WorkerGroup workerGroup) {
        return workerGroupMapper.updateByPrimaryKeySelective(workerGroup);
    }

    public int insert(WorkerGroup workerGroup){
        return workerGroupMapper.insertSelective(workerGroup);
    }

}
