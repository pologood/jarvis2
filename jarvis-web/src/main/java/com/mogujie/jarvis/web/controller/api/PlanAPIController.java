package com.mogujie.jarvis.web.controller.api;

import com.mogujie.jarvis.web.entity.vo.JobVo;
import com.mogujie.jarvis.web.entity.qo.PlanQo;
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

    /*
    * 分页获取执行计划
    * */
    @RequestMapping(value = "getPlans")
    @ResponseBody
    public Map<String, Object> getPlans(PlanQo planQo) {
        Map<String, Object> result = planService.getPlans(planQo);
        return result;
    }

    /*
    * 获取某个执行计划的依赖详情
    * */
    @RequestMapping(value = "/getDependDetail")
    @ResponseBody
    public TaskDependVo getDependDetail(Long taskId) {
        //查询依赖信息
        TaskDependVo taskDependVo = taskDependService.getTaskDependByTaskId(taskId);
        //如果有依赖信息，则生成前置、后续任务信息
        if (null != taskDependVo && null != taskDependVo.getTaskId()) {
            taskDependService.generate(taskDependVo);
        }
        //如果没有依赖信息，则只显示当前task自身信息
        else {
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
