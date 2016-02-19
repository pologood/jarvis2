package com.mogujie.jarvis.web.service;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.qo.PlanQo;
import com.mogujie.jarvis.web.entity.vo.PlanVo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
import com.mogujie.jarvis.web.mapper.PlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejian,muming on 15/10/21.
 */
@Service
public class PlanService {
    @Autowired
    PlanMapper planMapper;

    @Autowired
    TaskService taskService;

    /**
     * @param planQo
     * @func 根据条件获取执行计划
     * @author hejian
     */
    public Map<String, Object> getPlans(PlanQo planQo) {
        Map<String, Object> result = new HashMap<String, Object>();

        Integer total = planMapper.getPlanCountByCondition(planQo);
        List<PlanVo> planVoList = planMapper.getPlansByCondition(planQo);
        Map<Long, PlanVo> planVoMap = new HashMap<Long, PlanVo>();
        List<Long> jobIdList = new ArrayList<Long>();
        for (PlanVo planVo : planVoList) {
            planVoMap.put(planVo.getJobId(), planVo);
            jobIdList.add(planVo.getJobId());
        }

        List<TaskVo> recentTaskList = new ArrayList<TaskVo>();
        if (jobIdList.size() > 0) {
            recentTaskList = taskService.getTaskByJobIdBetweenTime(jobIdList, planQo.getScheduleStartTime(), planQo.getScheduleEndTime());
        }

        for (TaskVo taskVo : recentTaskList) {
            if (planVoMap.containsKey(taskVo.getJobId())) {
                PlanVo value = planVoMap.get(taskVo.getJobId());
                value.setTaskSize(value.getTaskSize() + 1);

                List<Object> status = value.getTaskStatus();
                JSONObject object = new JSONObject();
                object.put("taskId", taskVo.getTaskId());
                object.put("status", taskVo.getStatus());
                status.add(object);

                value.setTaskStatus(status);
            }
        }

        result.put("total", total);
        result.put("rows", planVoList);

        return result;
    }
}
