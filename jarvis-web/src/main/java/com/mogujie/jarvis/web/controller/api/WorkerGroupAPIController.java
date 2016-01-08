package com.mogujie.jarvis.web.controller.api;

import com.mogujie.jarvis.core.domain.WorkerGroupStatus;
import com.mogujie.jarvis.core.domain.WorkerStatus;
import com.mogujie.jarvis.web.entity.qo.WorkerGroupQo;
import com.mogujie.jarvis.web.service.WorkerGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejian on 16/1/8.
 */
@Controller
@RequestMapping(value = "/api/workerGroup")
public class WorkerGroupAPIController {
    @Autowired
    WorkerGroupService workerGroupService;

    @RequestMapping(value = "/getWorkerGroups")
    @ResponseBody
    public Map<String, Object> getWorkerGroups(WorkerGroupQo workerGroupSearchVo) {
        Map<String, Object> result;
        result = workerGroupService.getWorkerGroups(workerGroupSearchVo);
        return result;
    }

    @RequestMapping(value = "getWorkerGroupStatus")
    @ResponseBody
    public List<Map<String, Object>> getWorkerGroupStatus() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        WorkerGroupStatus[] workerGroupStatuses = WorkerGroupStatus.values();
        for (WorkerGroupStatus workerGroupStatus : workerGroupStatuses) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id",workerGroupStatus.getValue());
            map.put("text",workerGroupStatus.getDescription());
            list.add(map);
        }

        return list;
    }
}
