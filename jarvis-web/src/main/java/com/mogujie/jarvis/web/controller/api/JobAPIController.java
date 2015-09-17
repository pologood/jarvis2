package com.mogujie.jarvis.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.JobSearchVo;
import com.mogujie.jarvis.web.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JobService jobService;

    @RequestMapping("/getJobs")
    @ResponseBody
    public JSONObject getJobs(JobSearchVo jobSearchVo){

        JSONObject jobJson=jobService.getJobs(jobSearchVo);

        return jobJson;
    }
}
