/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年3月24日 上午11:11:06
 */

package com.mogujie.jarvis.server.service;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.dao.generate.DepartmentBizMapMapper;
import com.mogujie.jarvis.dao.generate.DepartmentMapper;
import com.mogujie.jarvis.dto.generate.Department;
import com.mogujie.jarvis.dto.generate.DepartmentBizMap;
import com.mogujie.jarvis.dto.generate.DepartmentBizMapExample;
import com.mogujie.jarvis.dto.generate.DepartmentBizMapKey;

/**
 * @author guangming
 *
 */
@Singleton
public class DepartmentService {

    @Inject
    private DepartmentMapper departmentMapper;

    @Inject
    private DepartmentBizMapMapper mapMapper;

    public Department get(int id) {
        return departmentMapper.selectByPrimaryKey(id);
    }

    public void insert(Department record) {
        departmentMapper.insert(record);
    }

    public void deleteDepartmen(int departmentId) {
        departmentMapper.deleteByPrimaryKey(departmentId);
        DepartmentBizMapExample example = new DepartmentBizMapExample();
        example.createCriteria().andDepartmentIdEqualTo(departmentId);
        mapMapper.deleteByExample(example);
    }

    public void update(Department record) {
        departmentMapper.updateByPrimaryKeySelective(record);
    }

    public void insertMap(DepartmentBizMap record) {
        mapMapper.insert(record);
    }

    public void deleteMap(int departmentId, int bizId) {
        DepartmentBizMapKey key = new DepartmentBizMapKey();
        key.setDepartmentId(departmentId);
        key.setBizId(bizId);
        mapMapper.deleteByPrimaryKey(key);
    }

    public List<Integer> getBizIdsByDepartmentId(int departmentId) {
        DepartmentBizMapExample example = new DepartmentBizMapExample();
        example.createCriteria().andDepartmentIdEqualTo(departmentId);
        List<DepartmentBizMap> maps = mapMapper.selectByExample(example);
        List<Integer> bizIds = new ArrayList<Integer>();
        if (maps != null) {
            for (DepartmentBizMap map : maps) {
                bizIds.add(map.getBizId());
            }
        }
        return bizIds;
    }
}
