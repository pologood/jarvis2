/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月10日 上午10:13:54
 */

package com.mogujie.jarvis.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.core.domain.CrontabType;
import com.mogujie.jarvis.dao.generate.CrontabMapper;
import com.mogujie.jarvis.dto.generate.Crontab;
import com.mogujie.jarvis.dto.generate.CrontabExample;

/**
 * @author guangming
 *
 */
@Service
public class CrontabService {
    @Autowired
    private CrontabMapper crontabMapper;

    public List<Crontab> getCronsByJobId(long jobId) {
        CrontabExample example = new CrontabExample();
        example.createCriteria().andJobIdEqualTo(jobId);
        List<Crontab> crontabs = crontabMapper.selectByExample(example);
        if (crontabs == null) {
            return new ArrayList<Crontab>();
        }
        return crontabs;
    }

    public void insert(long jobId, String expression) {
        Crontab record = new Crontab();
        record.setJobId(jobId);
        record.setCronExpression(expression);
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        record.setCreateTime(currentTime);
        record.setUpdateTime(currentTime);
        // TODO set crontype
        record.setCronType(CrontabType.POSITIVE.getValue());
        crontabMapper.insert(record);
    }

    public void updateOrDelete(long jobId, String expression) {
        Crontab record = getPositiveCrontab(jobId);
        if (record != null) {
            if (expression == null || expression.isEmpty()) {
                crontabMapper.deleteByPrimaryKey(record.getCronId());
            } else {
                record.setCronExpression(expression);
                DateTime dt = DateTime.now();
                Date currentTime = dt.toDate();
                record.setUpdateTime(currentTime);
                crontabMapper.updateByPrimaryKey(record);
            }
        } else {
            insert(jobId, expression);
        }
    }

    public void deleteByJobId(long jobId) {
        CrontabExample example = new CrontabExample();
        example.createCriteria().andJobIdEqualTo(jobId);
        crontabMapper.deleteByExample(example);
    }

    // 属于一个jobId的正向crontab只能有一个
    public Crontab getPositiveCrontab(long jobId) {
        CrontabExample example = new CrontabExample();
        example.createCriteria().andJobIdEqualTo(jobId).andCronTypeEqualTo(CrontabType.POSITIVE.getValue());
        List<Crontab> crontabs = crontabMapper.selectByExample(example);
        if (crontabs.size() > 0) {
            return crontabs.get(0);
        }
        return null;
    }
}
