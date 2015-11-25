/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月15日 下午2:57:15
 */

package com.mogujie.jarvis.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.mogujie.jarvis.core.domain.AppStatus;
import com.mogujie.jarvis.dao.generate.AppMapper;
import com.mogujie.jarvis.dao.generate.AppWorkerGroupMapper;
import com.mogujie.jarvis.dto.generate.App;
import com.mogujie.jarvis.dto.generate.AppExample;
import com.mogujie.jarvis.dto.generate.AppWorkerGroup;
import com.mogujie.jarvis.dto.generate.AppWorkerGroupExample;

/**
 * @author guangming
 *
 */
@Service
public class AppService {

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private AppWorkerGroupMapper appWorkerGroupMapper;

    public int getAppIdByName(String appName) {
        AppExample example = new AppExample();
        example.createCriteria().andAppNameEqualTo(appName);
        List<App> apps = appMapper.selectByExample(example);
        Preconditions.checkNotNull(apps, appName + "not found.");
        Preconditions.checkArgument(!apps.isEmpty(), appName + "not found.");
        return apps.get(0).getAppId();
    }

    public List<App> getAppList() {
        AppExample example = new AppExample();
        example.createCriteria().andStatusEqualTo(AppStatus.ENABLE.getValue());
        return appMapper.selectByExample(example);
    }

    /**
     * app能否访问workerGroup
     * @param appId         ：appId
     * @param workerGroupId ：workerGroupId
     * @return              ：
     */
    public boolean canAccessWorkerGroup(int appId, int workerGroupId) {

        AppWorkerGroupExample example = new AppWorkerGroupExample();
        example.createCriteria().andAppIdEqualTo(appId);

        List<AppWorkerGroup> list = appWorkerGroupMapper.selectByExample(example);
        for (AppWorkerGroup appWorkerGroup : list) {
            if (workerGroupId == appWorkerGroup.getWorkerGroupId()) {
                return true;
            }
        }

        return false;
    }

}
