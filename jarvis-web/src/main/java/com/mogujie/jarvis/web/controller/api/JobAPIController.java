package com.mogujie.jarvis.web.controller.api;

import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.web.entity.vo.JobSearchVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping(value = "/jarvis/api/job")
public class JobAPIController {

    @RequestMapping("/getJobs")
    @ResponseBody
    public List<Job> getJobs(JobSearchVo jobSearchVo){
        List<Job> jobList=new ArrayList<Job>();


        return jobList;
    }
}
