package com.mogujie.jarvis.web.service;

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
        Map<String, Object> result = new HashMap<>();

        Integer total = planMapper.getPlanCountByCondition(planQo);
        List<PlanVo> planVoList = planMapper.getPlansByCondition(planQo);
        Map<Long,PlanVo> planVoMap = new HashMap<>();
        List<Long> jobIdList = new ArrayList<>();
        for(PlanVo planVo : planVoList){
            planVoMap.put(planVo.getJobId(),planVo);
            jobIdList.add(planVo.getJobId());
        }

        List<TaskVo> recentTaskList = new ArrayList<>();
        if (jobIdList.size() > 0) {
            recentTaskList = taskService.getTaskByJobIdBetweenTime(jobIdList,planQo.getScheduleStartTime(),planQo.getScheduleEndTime());
        }

        for (TaskVo taskVo : recentTaskList) {
            if (planVoMap.containsKey(taskVo.getJobId())) {
                PlanVo value = planVoMap.get(taskVo.getJobId());
                value.setTaskSize(value.getTaskSize() + 1);

                String status = value.getTaskStatus();
                if (status == null || status.equals("")) {
                    status = taskVo.getStatus().toString();
                } else {
                    status = status + ',' + taskVo.getStatus().toString();
                }
                value.setTaskStatus(status);
            }
        }

        result.put("total", total);
        result.put("rows", planVoList);

        return result;
    }
}
