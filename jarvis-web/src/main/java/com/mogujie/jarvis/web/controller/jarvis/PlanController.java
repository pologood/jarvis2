package com.mogujie.jarvis.web.controller.jarvis;

import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import com.mogujie.jarvis.web.entity.qo.PlanQo;
import com.mogujie.jarvis.web.entity.vo.JobVo;
import com.mogujie.jarvis.web.entity.vo.TaskDependVo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
import com.mogujie.jarvis.web.service.JobService;
import com.mogujie.jarvis.web.service.TaskDependService;
import com.mogujie.jarvis.web.service.TaskService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping("/plan")
public class PlanController extends BaseController {
    @Autowired
    JobService jobService;
    @Autowired
    TaskService taskService;
    @Autowired
    TaskDependService taskDependService;

    /**
     * 执行计划首页
     */
    @RequestMapping
    @JarvisPassport(authTypes = JarvisAuthType.plan)
    public String index(ModelMap modelMap) {

        List<Long> jobIdList = jobService.getJobIds();
        List<String> jobNameList = jobService.getJobNames();
        List<String> executeUserList = taskService.getAllExecuteUser();

        PlanQo planQo = new PlanQo();
        planQo.setScheduleDate(DateTime.now().toString("yyyyMMdd"));

        modelMap.put("jobIdList", jobIdList);
        modelMap.put("jobNameList", jobNameList);
        modelMap.put("executeUserList", executeUserList);
        modelMap.put("planQo", JsonHelper.toJson(planQo));
        return "plan/index";
    }
}
