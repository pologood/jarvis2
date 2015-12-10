package com.mogujie.jarvis.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.TaskQo;
import com.mogujie.jarvis.web.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hejian on 15/9/17.
 */
@Controller
@RequestMapping(value = "/jarvis/api/task")
public class TaskAPIController {
    @Autowired
    TaskService taskService;

    @RequestMapping(value = "/getTasks")
    @ResponseBody
    public JSONObject getTasks(TaskQo taskQo){
        JSONObject jsonObject = taskService.getTasks(taskQo);

        return jsonObject;
    }



}
