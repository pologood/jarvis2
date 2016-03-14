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



        List<String> taskStatusList=planQo.getTaskStatusList();
        //包含未初始化的job
        for(String status:taskStatusList){
            if(status.equals("0")){
                planQo.setUnInitial(true);
                break;
            }
        }

        Integer total = planMapper.getPlanCountByCondition(planQo);
        List<PlanVo> planVoList = planMapper.getPlansByCondition(planQo);



        result.put("total", total);
        result.put("rows", planVoList);

        return result;
    }
}
