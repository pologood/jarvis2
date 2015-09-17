package com.mogujie.jarvis.web.controller.jarvis;

import com.mogujie.jarvis.web.entity.vo.JobVo;
import com.mogujie.jarvis.web.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping("/jarvis/job")
public class JobController extends BaseController{

    @Autowired
    JobService jobService;

    @RequestMapping
    public String index(ModelMap modelMap){
        List<Long> jobIds= jobService.getJobIds();
        List<String> jobNames= jobService.getJobNames();
        List<String> submitUsers= jobService.getSubmitUsers();

        modelMap.put("jobIds",jobIds);
        modelMap.put("jobNames",jobNames);
        modelMap.put("submitUsers",submitUsers);

        return "job/index";
    }

    @RequestMapping(value = "addOrEdit")
    public String addOrEdit(ModelMap modelMap,Long jobId){
        if(jobId!=null){
            JobVo jobVo=jobService.getJobById(jobId);
            modelMap.put("jobVo",jobVo);
        }

        return "job/addOrEdit";
    }

    @RequestMapping(value = "dependency")
    public String dependency(ModelMap modelMap){

        return "job/dependency";
    }

    @RequestMapping(value = "trash")
    public String trash(ModelMap modelMap){

        return "job/trash";
    }
}
