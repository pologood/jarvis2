package com.mogujie.jarvis.web.controller.api;

import com.mogujie.jarvis.web.entity.qo.WorkerGroupQo;
import com.mogujie.jarvis.web.entity.qo.WorkerQo;
import com.mogujie.jarvis.web.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

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
    public Map<String, Object> getWorkers(WorkerQo workerSearchVo) {
        Map<String, Object> result;
        result = workerService.getWorkers(workerSearchVo);
        return result;
    }

    @RequestMapping(value = "/getWorkerGroups")
    @ResponseBody
    public Map<String, Object> getWorkerGroups(WorkerGroupQo workerGroupSearchVo) {
        Map<String, Object> result;
        result = workerService.getWorkerGroups(workerGroupSearchVo);
        return result;
    }
}
