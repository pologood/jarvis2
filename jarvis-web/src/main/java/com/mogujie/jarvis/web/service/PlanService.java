package com.mogujie.jarvis.web.service;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.PlanQo;
import com.mogujie.jarvis.web.entity.vo.PlanVo;
import com.mogujie.jarvis.web.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hejian on 15/10/21.
 */
@Service
public class PlanService {
    @Autowired
    TaskMapper taskMapper;

    /**
     * @func 根据条件获取执行计划
     * @author hejian
     * @param planQo
     * */
    public JSONObject getPlans(PlanQo planQo){
        JSONObject jsonObject = new JSONObject();

        Integer total = taskMapper.getPlanCountByCondition(planQo);
        List<PlanVo> planVoList = taskMapper.getPlansByCondition(planQo);

        jsonObject.put("total",total);
        jsonObject.put("rows",planVoList);

        return jsonObject;
    }
}
