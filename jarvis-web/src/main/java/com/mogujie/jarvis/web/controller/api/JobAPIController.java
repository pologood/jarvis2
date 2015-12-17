package com.mogujie.jarvis.web.controller.api;

import com.mogu.bigdata.admin.core.entity.vo.UserInfo;
import com.mogu.bigdata.admin.inside.service.AdminUserService;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.web.entity.qo.JobQo;
import com.mogujie.jarvis.web.service.JobDependService;
import com.mogujie.jarvis.web.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @Autowired
    protected AdminUserService userService;

    @RequestMapping("/getJobs")
    @ResponseBody
    public Map<String, Object> getJobs(JobQo jobQo) {

        Map<String, Object> result = jobService.getJobs(jobQo);

        return result;
    }

    @RequestMapping("/getAllUser")
    @ResponseBody
    public List<UserInfo> getAllUser(ModelMap mp) {
        List<UserInfo> userInfoList = null;
        try {
            userInfoList = userService.getAllUsers();
        } catch (Exception e) {
            e.printStackTrace();
            userInfoList = new ArrayList<UserInfo>();
        }
        return userInfoList;
    }

    /**
     * 单向依赖树
     */
    @RequestMapping("/getTreeDependedONJob")
    @ResponseBody
    public Map<String, Object> getTreeDependedONJob(JobQo jobQo) {

        Map<String, Object> result = jobDependService.getTreeDependedOnJob(jobQo);

        return result;
    }

    /**
     * 两向树
     */
    @RequestMapping("/getTwoDirectionTree")
    @ResponseBody
    public Map<String, Object> getTwoDirectionTree(JobQo jobSearchVo) {

        Map<String, Object> result = jobDependService.getTwoDirectionTreeDependedOnJob(jobSearchVo);

        return result;
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
