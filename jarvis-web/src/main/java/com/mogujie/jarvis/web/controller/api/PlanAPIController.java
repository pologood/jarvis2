package com.mogujie.jarvis.web.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.JobVo;
import com.mogujie.jarvis.web.entity.vo.PlanQo;
import com.mogujie.jarvis.web.entity.vo.TaskDependVo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
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
 * Created by hejian on 15/10/21.
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

    @RequestMapping(value = "getPlans")
    @ResponseBody
    public Map<String, Object> getPlans(PlanQo planQo) {
        Map<String, Object> result = planService.getPlans(planQo);
        return result;
    }

    @RequestMapping(value = "/getDependDetail")
    @ResponseBody
    public TaskDependVo getDependDetail(Long taskId) {
        TaskDependVo taskDependVo = taskDependService.getTaskDependByTaskId(taskId);
        if (taskDependVo != null) {
            taskDependService.generate(taskDependVo);
        } else {
            TaskVo taskVo = taskService.getTaskById(taskId);
            JobVo jobVo = jobService.getJobById(taskVo.getJobId());

            taskDependVo = new TaskDependVo();
            taskDependVo.setTaskId(taskId);
            taskDependVo.setJobId(taskVo.getJobId());
            taskDependVo.setJobName(jobVo.getJobName());
            taskDependVo.setStatus(taskVo.getStatus());
            taskDependVo.setExecuteUser(taskVo.getExecuteUser());
            taskDependVo.setRootFlag(true);
            taskDependVo.setScheduleTime(taskVo.getScheduleTime());
            taskDependVo.setParentFlag(false);
        }

        return taskDependVo;
    }
}
