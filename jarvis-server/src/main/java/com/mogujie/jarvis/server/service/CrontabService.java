/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月10日 上午10:13:54
 */

package com.mogujie.jarvis.server.service;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.core.domain.CrontabType;
import com.mogujie.jarvis.dao.CrontabMapper;
import com.mogujie.jarvis.dto.Crontab;
import com.mogujie.jarvis.dto.CrontabExample;

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
        Crontab crontab = new Crontab();
        crontab.setJobId(jobId);
        crontab.setExp(expression);
        Date currentTime = new Date();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
        dateTimeFormat.format(currentTime);
        crontab.setCreateTime(currentTime);
        crontab.setUpdateTime(currentTime);
        //TODO set crontype
        crontab.setCronType(CrontabType.POSITIVE.getValue());
    }

    public void update(long jobId, String expression) {
        Crontab crontab = getUniqueCrontab(jobId);
        crontab.setExp(expression);
        Date currentTime = new Date();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
        dateTimeFormat.format(currentTime);
        crontab.setUpdateTime(currentTime);
    }

    private Crontab getUniqueCrontab(long jobId) {
        //TODO jobID为正向的cron只能有唯一的一个
        return null;
    }
}
