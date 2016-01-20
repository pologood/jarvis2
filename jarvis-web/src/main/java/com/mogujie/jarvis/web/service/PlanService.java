package com.mogujie.jarvis.web.service;

import com.mogujie.jarvis.web.entity.qo.PlanQo;
import com.mogujie.jarvis.web.entity.vo.PlanVo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
import com.mogujie.jarvis.web.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejian on 15/10/21.
 */
@Service
public class PlanService {
    @Autowired
    TaskMapper taskMapper;

    /**
     * @param planQo
     * @func 根据条件获取执行计划
     * @author hejian
     */
    public Map<String, Object> getPlans(PlanQo planQo) {
        Map<String, Object> result = new HashMap<String, Object>();

        Integer total = taskMapper.getPlanCountByCondition(planQo);
        List<PlanVo> planVoList = taskMapper.getPlansByCondition(planQo);

        List<Long> jobIdList = new ArrayList<Long>();
        for (PlanVo planVo : planVoList) {
            jobIdList.add(planVo.getJobId());
        }

        List<PlanVo> recentPlanList = new ArrayList<PlanVo>();
        if (jobIdList.size() > 0) {
            recentPlanList = taskMapper.getRecentExecuteTaskByJobId(jobIdList);
        }

        Map<Long, Map<String, Long>> avgTimeMap = new HashMap<Long, Map<String, Long>>();
        for (PlanVo planVo : recentPlanList) {
            if (avgTimeMap.containsKey(planVo.getJobId())) {
                Map<String, Long> map = avgTimeMap.get(planVo.getJobId());
                Long size = map.get("size");
                Long avgTime = map.get("avgTime");
                if (null == avgTime) {
                    avgTime = 0l;
                }
                Long newSize = size + 1;
                Long newAvgTime=avgTime;
                if(null!=planVo.getExecuteTime()){
                    newAvgTime = ((avgTime * size) + planVo.getExecuteTime()) / newSize;
                }
                map.put("size", newSize);
                map.put("avgTime", newAvgTime);
            } else {
                Map<String, Long> map = new HashMap<String, Long>();
                map.put("size", 1l);
                map.put("avgTime", planVo.getExecuteTime());
                avgTimeMap.put(planVo.getJobId(), map);
            }
        }

        for (PlanVo planVo : planVoList) {
            if (planVo.getStatus().equals(1) || planVo.getStatus().equals(2) || planVo.getStatus().equals(3)) {
                planVo.setPredictExecuteTime(avgTimeMap.get(planVo.getJobId()).get("avgTime"));
            }
        }


        result.put("total", total);
        result.put("rows", planVoList);

        return result;
    }
}
