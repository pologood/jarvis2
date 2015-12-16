package com.mogujie.jarvis.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.TaskQo;
import com.mogujie.jarvis.web.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by hejian on 15/9/17.
 */
@Controller
@RequestMapping(value = "/api/task")
public class TaskAPIController {
    @Autowired
    TaskService taskService;

    @RequestMapping(value = "/getTasks")
    @ResponseBody
    public Map<String, Object> getTasks(TaskQo taskQo) {
        Map<String, Object> result = taskService.getTasks(taskQo);

        return result;
    }


}
