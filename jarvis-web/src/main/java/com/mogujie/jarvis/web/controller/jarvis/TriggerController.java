package com.mogujie.jarvis.web.controller.jarvis;

import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import com.mogujie.jarvis.web.entity.vo.JobSearchVo;
import com.mogujie.jarvis.web.entity.vo.JobVo;
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
@RequestMapping("/jarvis/trigger")
public class TriggerController extends BaseController{

    @Autowired
    JobService jobService;

    @RequestMapping
    @JarvisPassport(authTypes = JarvisAuthType.trigger)
    public String index(ModelMap modelMap){
        List<JobVo> jobVoList=jobService.getAllJobs();

        modelMap.put("jobVoList",jobVoList);
        return "trigger/index";
    }


}
