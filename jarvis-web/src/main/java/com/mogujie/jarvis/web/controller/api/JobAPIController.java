package com.mogujie.jarvis.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.JobQo;
import com.mogujie.jarvis.web.service.JobDependService;
import com.mogujie.jarvis.web.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping(value = "/jarvis/api/job")
public class JobAPIController {

    @Autowired
    private JobService jobService;
    @Autowired
    private JobDependService jobDependService;

    @RequestMapping("/getJobs")
    @ResponseBody
    public JSONObject getJobs(JobQo jobQo){

        JSONObject jobJson=jobService.getJobs(jobQo);

        return jobJson;
    }

    /**
     * 单向依赖树
     * */
    @RequestMapping("/getTreeDependedONJob")
    @ResponseBody
    public JSONObject getTreeDependedONJob(JobQo jobSearchVo){

        JSONObject jobJson=jobDependService.getTreeDependedOnJob(jobSearchVo);

        return jobJson;
    }

    /**
     * 两向树
     * */
    @RequestMapping("/getTwoDirectionTree")
    @ResponseBody
    public JSONObject getTwoDirectionTree(JobQo jobSearchVo){

        JSONObject jobJson=jobDependService.getTwoDirectionTreeDependedOnJob(jobSearchVo);

        return jobJson;
    }

    /**
     * 相似jobId
     *
     * */
    @RequestMapping("/getSimilarJobIds")
    @ResponseBody
    public JSONObject getSimilarJobIds(Long q){
        JSONObject jobJson=jobService.getSimilarJobIds(q);
        return jobJson;
    }

    /**
     * 相似jobName
     *
     * */
    @RequestMapping("/getSimilarJobNames")
    @ResponseBody
    public JSONObject getSimilarJobNames(String q){
        JSONObject jobJson=jobService.getSimilarJobNames(q);
        return jobJson;
    }
}
