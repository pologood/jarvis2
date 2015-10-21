/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月20日 下午6:41:21
 */

package com.mogujie.jarvis.server.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.mogujie.jarvis.dao.PlanMapper;
import com.mogujie.jarvis.dto.Plan;
import com.mogujie.jarvis.dto.PlanExample;

/**
 * @author guangming
 *
 */
public class PlanService {
    @Autowired
    private PlanMapper planMapper;

    public List<Plan> getAllPlans() {
        PlanExample example = new PlanExample();
        return planMapper.selectByExample(example);
    }

    public Plan getTodayPlan(long jobId, Date planDate) {
        PlanExample example = new PlanExample();
        example.createCriteria().andJobIdEqualTo(jobId).andPlanDateEqualTo(planDate);
        List<Plan> records = planMapper.selectByExample(example);
        Plan uniquePlan = null;
        if (records != null && !records.isEmpty()) {
            uniquePlan = records.get(0);
        }
        return uniquePlan;
    }
}
