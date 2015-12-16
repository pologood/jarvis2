package com.mogujie.jarvis.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.JobQo;
import com.mogujie.jarvis.web.service.JobDependService;
import com.mogujie.jarvis.web.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping(value = "/api/job")
public class JobAPIController {

    @Autowired
    private JobService jobService;
    @Autowired
    private JobDependService jobDependService;

    @RequestMapping("/getJobs")
    @ResponseBody
    public Map<String, Object> getJobs(JobQo jobQo) {

        Map<String, Object> result = jobService.getJobs(jobQo);

        return result;
    }

    /**
     * 单向依赖树
     */
    @RequestMapping("/getTreeDependedONJob")
    @ResponseBody
    public JSONObject getTreeDependedONJob(JobQo jobQo) {

        JSONObject jobJson = jobDependService.getTreeDependedOnJob(jobQo);

        return jobJson;
    }

    /**
     * 两向树
     */
    @RequestMapping("/getTwoDirectionTree")
    @ResponseBody
    public JSONObject getTwoDirectionTree(JobQo jobSearchVo) {

        JSONObject jobJson = jobDependService.getTwoDirectionTreeDependedOnJob(jobSearchVo);

        return jobJson;
    }

    /**
     * 相似jobId
     */
    @RequestMapping("/getSimilarJobIds")
    @ResponseBody
    public Map<String, Object> getSimilarJobIds(Long q) {
        Map<String, Object> result = jobService.getSimilarJobIds(q);
        return result;
    }

    /**
     * 相似jobName
     */
    @RequestMapping("/getSimilarJobNames")
    @ResponseBody
    public Map<String, Object> getSimilarJobNames(String q) {
        Map<String, Object> result = jobService.getSimilarJobNames(q);
        return result;
    }

    /**
     * 相似jobName
     */
    @RequestMapping("/getJobBySimilarNames")
    @ResponseBody
    public Map<String, Object> getJobBySimilarNames(String q) {
        Map<String, Object> result = jobService.getJobBySimilarNames(q);
        return result;
    }
}
