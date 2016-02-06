package com.mogujie.jarvis.web.controller.api;

import com.mogujie.jarvis.web.entity.qo.PlanQo;
import com.mogujie.jarvis.web.service.JobService;
import com.mogujie.jarvis.web.service.PlanService;
import com.mogujie.jarvis.web.service.TaskDependService;
import com.mogujie.jarvis.web.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by hejian,muming on 15/10/21.
 */
@Controller
@RequestMapping(value = "/api/plan")
public class PlanAPIController {
    @Autowired
    PlanService planService;
    @Autowired
    TaskDependService taskDependService;
    @Autowired
    TaskService taskService;
    @Autowired
    JobService jobService;

    /*
    * 分页获取执行计划
    * */
    @RequestMapping(value = "getPlans")
    @ResponseBody
    public Object getPlans(PlanQo planQo) {
        Map<String, Object> result = planService.getPlans(planQo);
        return result;
    }

}
