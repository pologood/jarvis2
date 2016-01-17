package com.mogujie.jarvis.server.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.core.domain.WorkerGroupStatus;
import com.mogujie.jarvis.core.exception.NotFoundException;
import com.mogujie.jarvis.dao.generate.AppWorkerGroupMapper;
import com.mogujie.jarvis.dao.generate.WorkerGroupMapper;
import com.mogujie.jarvis.dto.generate.*;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * @author muming
 */
@Singleton
public class AppWorkerGroupService {

    @Inject
    private AppWorkerGroupMapper appWorkerGroupMapper;

    public AppWorkerGroup get4ReturnNull(Integer appID,Integer workerGroupId){
        AppWorkerGroupKey key = new AppWorkerGroupKey();
        key.setAppId(appID);
        key.setWorkerGroupId(workerGroupId);
        return appWorkerGroupMapper.selectByPrimaryKey(key);
    }

    public int insert(@NotNull AppWorkerGroup appWorkerGroup) {
        return appWorkerGroupMapper.insertSelective(appWorkerGroup);
    }

    public int delete(Integer appID,Integer workerGroupId) {
        AppWorkerGroupKey key = new AppWorkerGroupKey();
        key.setAppId(appID);
        key.setWorkerGroupId(workerGroupId);
        return appWorkerGroupMapper.deleteByPrimaryKey(key);
    }




}
