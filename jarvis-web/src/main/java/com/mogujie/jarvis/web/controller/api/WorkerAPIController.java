package com.mogujie.jarvis.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.WorkerGroupQo;
import com.mogujie.jarvis.web.entity.vo.WorkerQo;
import com.mogujie.jarvis.web.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hejian on 15/9/28.
 */
@Controller
@RequestMapping(value = "/api/worker")
public class WorkerAPIController {
    @Autowired
    WorkerService workerService;

    @RequestMapping(value = "/getWorkers")
    @ResponseBody
    public JSONObject getWorkers(ModelMap modelMap,WorkerQo workerSearchVo){
        JSONObject result;
        result=workerService.getWorkers(workerSearchVo);
        return result;
    }

    @RequestMapping(value = "/getWorkerGroups")
    @ResponseBody
    public JSONObject getWorkerGroups(ModelMap modelMap,WorkerGroupQo workerGroupSearchVo){
        JSONObject result;
        result=workerService.getWorkerGroups(workerGroupSearchVo);
        return result;
    }
}
