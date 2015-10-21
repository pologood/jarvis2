package com.mogujie.jarvis.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.PlanSearchVo;
import com.mogujie.jarvis.web.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hejian on 15/10/21.
 */
@Controller
@RequestMapping(value = "/jarvis/api/plan")
public class PlanAPIController {
    @Autowired
    PlanService planService;

    @RequestMapping(value = "getPlans")
    @ResponseBody
    public JSONObject getPlans(PlanSearchVo planSearchVo){
        JSONObject jsonObject=planService.getPlans(planSearchVo);
        return jsonObject;
    }
}
