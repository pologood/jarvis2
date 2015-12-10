package com.mogujie.jarvis.web.controller.jarvis;

import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import com.mogujie.jarvis.web.entity.vo.JobVo;
import com.mogujie.jarvis.web.entity.vo.TaskDependVo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
import com.mogujie.jarvis.web.service.JobService;
import com.mogujie.jarvis.web.service.TaskDependService;
import com.mogujie.jarvis.web.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping("/jarvis/plan")
public class PlanController extends BaseController{
    @Autowired
    JobService jobService;
    @Autowired
    TaskService taskService;
    @Autowired
    TaskDependService taskDependService;

    /**
     * 执行计划首页
     *
     * */
    @RequestMapping
    @JarvisPassport(authTypes = JarvisAuthType.plan)
    public String index(ModelMap modelMap){

        List<Long> jobIdList=jobService.getJobIds();
        List<String> jobNameList=jobService.getJobNames();

        List<String> executeUserList=taskService.getAllExecuteUser();
        modelMap.put("jobIdList",jobIdList);
        modelMap.put("jobNameList",jobNameList);
        modelMap.put("executeUserList",executeUserList);
        return "plan/index";
    }

    /**
     * 执行计划详情，前置、后续计划执行状态
     **/
    @RequestMapping(value = "dependency")
    public String dependency(ModelMap modelMap,Long taskId){

        TaskVo taskVo=taskService.getTaskById(taskId);
        JobVo jobVo=jobService.getJobById(taskVo.getJobId());

        modelMap.put("taskId",taskId);
        modelMap.put("jobVo",jobVo);
        return "plan/dependency";
    }
}
