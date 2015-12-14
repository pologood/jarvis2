/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月15日 下午2:57:15
 */

package com.mogujie.jarvis.server.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mogujie.jarvis.dao.generate.AppMapper;
import com.mogujie.jarvis.dao.generate.AppWorkerGroupMapper;
import com.mogujie.jarvis.dto.generate.App;
import com.mogujie.jarvis.dto.generate.AppExample;
import com.mogujie.jarvis.dto.generate.AppWorkerGroup;
import com.mogujie.jarvis.dto.generate.AppWorkerGroupExample;

/**
 * @author guangming
 */
@Service
public class AppService {

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private AppWorkerGroupMapper appWorkerGroupMapper;

    private Map<Integer, App> appMetastore = Maps.newConcurrentMap();

    @PostConstruct
    private void init() {
        AppExample example = new AppExample();
        List<App> apps = appMapper.selectByExample(example);
        for (App app : apps) {
            appMetastore.put(app.getAppId(), app);
        }
    }

    public int getAppIdByName(String appName) {
        return getAppByName(appName).getAppId();
    }

    public App getAppByName(String appName) {
        Preconditions.checkNotNull(appName, appName + "not found.");
        for (Entry<Integer, App> entry : appMetastore.entrySet()) {
            App app = entry.getValue();
            if (app.getAppName().equals(appName)) {
                return app;
            }
        }

        return null;
    }

    public List<App> getAppList() {
        List<App> list = Lists.newArrayList(appMetastore.values());
        return list;
    }

    public void removeApp(int appId) {
        appMetastore.remove(appId);
    }

    public void add(App app) {
        appMetastore.put(app.getAppId(), app);
    }

    public void update(App app) {
        int appId = app.getAppId();
        App srcApp = appMetastore.get(appId);
        if (srcApp == null) {
            appMetastore.put(appId, app);
        } else {
            Field[] fields = app.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                try {
                    Object value = field.get(app);
                    if (value != null) {
                        field.set(srcApp, value);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    Throwables.propagate(e);
                }
            }
        }
    }

    /**
     * app能否访问workerGroup
     *
     * @param appId         ：appId
     * @param workerGroupId ：workerGroupId
     * @return ：
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
