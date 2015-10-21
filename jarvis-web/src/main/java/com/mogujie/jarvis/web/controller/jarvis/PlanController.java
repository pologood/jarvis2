package com.mogujie.jarvis.web.controller.jarvis;

import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import com.mogujie.jarvis.web.service.JobService;
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

    @RequestMapping
    @JarvisPassport(authTypes = JarvisAuthType.plan)
    public String index(ModelMap modelMap){
        List<String> submitUserList=jobService.getSubmitUsers();
        modelMap.put("submitUserList",submitUserList);
        return "plan/index";
    }

    @RequestMapping(value = "dependency")
    public String detail(ModelMap modelMap,Long jobId){

        return "plan/dependency";
    }
}
