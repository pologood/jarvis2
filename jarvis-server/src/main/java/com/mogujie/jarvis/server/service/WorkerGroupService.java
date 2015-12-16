package com.mogujie.jarvis.server.service;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.dao.generate.WorkerGroupMapper;
import com.mogujie.jarvis.dto.generate.WorkerGroup;
import com.mogujie.jarvis.dto.generate.WorkerGroupExample;

/**
 * @author muming
 */
@Singleton
public class WorkerGroupService {

    @Inject
    private WorkerGroupMapper workerGroupMapper;

    public int update(WorkerGroup workerGroup) {
        return workerGroupMapper.updateByPrimaryKeySelective(workerGroup);
    }

    public int insert(WorkerGroup workerGroup) {
        return workerGroupMapper.insertSelective(workerGroup);
    }

    public int getGroupIdByAuthKey(String key) {
        WorkerGroupExample example = new WorkerGroupExample();
        example.createCriteria().andAuthKeyEqualTo(key);
        List<WorkerGroup> list = workerGroupMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0).getId();
        }
        return 0;
    }

    public WorkerGroup getGroupByGroupId(int groupId) {
        return workerGroupMapper.selectByPrimaryKey(groupId);
    }

}
