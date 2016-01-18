/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming
 * Create Date: 2015年12月16日 下午5:59:26
 */

package com.mogujie.jarvis.server.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.exception.NotFoundException;
import com.mogujie.jarvis.dao.generate.BizGroupMapper;
import com.mogujie.jarvis.dao.generate.JobMapper;
import com.mogujie.jarvis.dto.generate.BizGroup;
import com.mogujie.jarvis.dto.generate.BizGroupExample;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.JobExample;

import java.util.List;

/**
 * BizGroupService
 *
 * @author muming
 */
@Singleton
public class BizGroupService {

    @Inject
    private BizGroupMapper bizGroupMapper;
    @Inject
    private JobMapper jobMapper;

    /**
     * 检查——名字重复
     *
     * @param name
     * @return
     */
    public void checkDuplicateName(String name) throws IllegalArgumentException {
        BizGroupExample example = new BizGroupExample();
        example.createCriteria().andNameEqualTo(name);
        List<BizGroup> list = bizGroupMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            throw new IllegalArgumentException("bizGroup 名称重复了. name:" + name);
        }
    }

    public BizGroup get(Integer id) throws NotFoundException {
        BizGroup bizGroup = bizGroupMapper.selectByPrimaryKey(id);
        if (bizGroup == null) {
            throw new NotFoundException("bizGroup 不存在. id:" + id);
        }
        return bizGroup;
    }

    public BizGroup queryById(Integer id) {
        return bizGroupMapper.selectByPrimaryKey(id);
    }


    public int insert(BizGroup bg) {
        return bizGroupMapper.insertSelective(bg);
    }

    public int update(BizGroup bg) {
        return bizGroupMapper.updateByPrimaryKeySelective(bg);
    }

    public int delete(Integer id) {
        return bizGroupMapper.deleteByPrimaryKey(id);
    }


    public void checkDeletable(Integer id) {
        JobExample example = new JobExample();
        example.createCriteria().andBizGroupIdEqualTo(id).andStatusNotEqualTo(JobStatus.DELETED.getValue());
        List<Job> list = jobMapper.selectByExample(example);
        if(list != null &&  list.size() >0){
            throw new IllegalArgumentException("bizGroupId在job中还在使用,不能删除. id:" + id);
        }
    }

}
