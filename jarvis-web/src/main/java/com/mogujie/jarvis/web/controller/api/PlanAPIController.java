package com.mogujie.jarvis.web.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.PlanQo;
import com.mogujie.jarvis.web.entity.vo.TaskDependVo;
import com.mogujie.jarvis.web.service.PlanService;
import com.mogujie.jarvis.web.service.TaskDependService;
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
    @Autowired
    TaskDependService taskDependService;

    @RequestMapping(value = "getPlans")
    @ResponseBody
    public JSONObject getPlans(PlanQo planQo){
        JSONObject jsonObject=planService.getPlans(planQo);
        return jsonObject;
    }

    @RequestMapping(value = "/getDependDetail")
    @ResponseBody
    public JSONObject getDependDetail(Long taskId){
        JSONObject jsonObject=new JSONObject();

        TaskDependVo taskDependVo=taskDependService.getTaskDependByTaskId(taskId);
        taskDependService.generate(taskDependVo);

        jsonObject = (JSONObject)JSON.toJSON(taskDependVo);

        return jsonObject;
    }
}
