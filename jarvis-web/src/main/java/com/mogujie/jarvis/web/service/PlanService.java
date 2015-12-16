package com.mogujie.jarvis.web.service;

import com.mogujie.jarvis.web.entity.vo.PlanQo;
import com.mogujie.jarvis.web.entity.vo.PlanVo;
import com.mogujie.jarvis.web.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        result.put("total", total);
        result.put("rows", planVoList);

        return result;
    }
}
