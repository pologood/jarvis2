package com.mogujie.jarvis.web.controller.api;

import com.mogu.bigdata.admin.core.entity.User;
import com.mogu.bigdata.admin.core.entity.vo.UserInfo;
import com.mogu.bigdata.admin.inside.service.AdminUserService;
import com.mogujie.jarvis.core.domain.CommonStrategy;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.expression.ScheduleExpressionType;
import com.mogujie.jarvis.web.entity.qo.JobQo;
import com.mogujie.jarvis.web.entity.vo.JobDependVo;
import com.mogujie.jarvis.web.entity.vo.JobVo;
import com.mogujie.jarvis.web.service.JobDependService;
import com.mogujie.jarvis.web.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

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

    /*
    * 根据id获取job的详细信息
    * */
    @RequestMapping("getById")
    @ResponseBody
    public Object getById(JobQo jobQo) {
        JobVo jobVo = jobService.getJobById(jobQo.getJobId());
        return jobVo;
    }

    /*
    * 根据id获取job的父任务
    * */
    @RequestMapping("getParentsById")
    @ResponseBody
    public List getParentsById(JobQo jobQo) {
        List<JobDependVo> list = jobDependService.getParentById(jobQo.getJobId());
        return list;
    }


    @RequestMapping("/getJobs")
    @ResponseBody
    public Map<String, Object> getJobs(JobQo jobQo) {
        Map<String, Object> result = jobService.getJobs(jobQo);
        return result;
    }

    @RequestMapping("/getAllUser")
    @ResponseBody
    public List<User> getAllUser() {
        List<User> userList;
        try {
            userList = userService.getAllUsers();
        } catch (Exception e) {
            e.printStackTrace();
            userList = new ArrayList<User>();
        }
        return userList;
    }

    /*
    * 提交过job的用户
    * */
    @RequestMapping(value = "getSubmitUsers")
    @ResponseBody
    public List getSubmitUsers() {
        List<String> submitUsers = jobService.getSubmitUsers();
        return submitUsers;
    }

    @RequestMapping(value = "getAllJobIdAndName")
    @ResponseBody
    public List getAllJobs() {
        List<Integer> statusList = new ArrayList<Integer>();
        List<Map> list = jobService.getAllJobIdAndName(statusList);
        return list;
    }


    /**
     * 单向依赖树
     */
    @RequestMapping("/getTreeDependedOnJob")
    @ResponseBody
    public Map<String, Object> getTreeDependedOnJob(JobQo jobQo) {
        Map<String, Object> result = jobDependService.getTreeDependedOnJob(jobQo);
        return result;
    }

    /**
     * 两向树
     */
    @RequestMapping("/getTwoDirectionTree")
    @ResponseBody
    public Map<String, Object> getTwoDirectionTree(JobQo jobQo) {
        Map<String, Object> result = jobDependService.getTwoDirectionTreeDependedOnJob(jobQo);
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


    @RequestMapping(value = "getJobStatus")
    @ResponseBody
    public List<Map<String, Object>> getJobStatus() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        JobStatus[] jobStatuses = JobStatus.values();
        for (JobStatus jobStatus : jobStatuses) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", jobStatus.getValue());
            map.put("text", jobStatus.getDescription());
            list.add(map);
        }

        return list;
    }

    @RequestMapping(value = "getExpressionType")
    @ResponseBody
    public List<Map<String, Object>> getExpressionType() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        ScheduleExpressionType[] scheduleExpressionTypes = ScheduleExpressionType.values();
        for (ScheduleExpressionType scheduleExpressionType : scheduleExpressionTypes) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", scheduleExpressionType.getValue());
            map.put("text", scheduleExpressionType.getDescription());
            list.add(map);
        }

        return list;
    }

    @RequestMapping(value = "getCommonStrategy")
    @ResponseBody
    public List<Map<String, Object>> getCommonStrategy() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        CommonStrategy[] commonStrategies = CommonStrategy.values();
        for (CommonStrategy commonStrategy : commonStrategies) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", commonStrategy.getValue());
            map.put("text", commonStrategy.getDescription());
            list.add(map);
        }

        return list;
    }


}
