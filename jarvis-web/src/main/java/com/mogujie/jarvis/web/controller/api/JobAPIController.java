package com.mogujie.jarvis.web.controller.api;

import com.mogu.bigdata.admin.core.entity.User;
import com.mogu.bigdata.admin.core.entity.vo.UserInfo;
import com.mogu.bigdata.admin.inside.service.AdminUserService;
import com.mogujie.jarvis.core.domain.CommonStrategy;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.expression.ScheduleExpressionType;
import com.mogujie.jarvis.web.entity.qo.JobQo;
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

    @RequestMapping("/getJobs")
    @ResponseBody
    public Map<String, Object> getJobs(JobQo jobQo) {

        Map<String, Object> result = jobService.getJobs(jobQo);
        JobStatus[] jobStatuses=JobStatus.values();

        return result;
    }

    @RequestMapping("/getAllUser")
    @ResponseBody
    public List<User> getAllUser(ModelMap mp) {
        List<User> userList = null;
        try {
            userList = userService.getAllUsers();
        } catch (Exception e) {
            e.printStackTrace();
            userList = new ArrayList<User>();
        }
        return userList;
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


    @RequestMapping(value = "getJobStatus")
    @ResponseBody
    public List<Map<String, Object>> getJobStatus() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        JobStatus[] jobStatuses = JobStatus.values();
        for (JobStatus jobStatus : jobStatuses) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id",jobStatus.getValue());
            map.put("text",jobStatus.getDescription());
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
            map.put("id",scheduleExpressionType.getValue());
            map.put("text",scheduleExpressionType.getDescription());
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
            map.put("id",commonStrategy.getValue());
            map.put("text",commonStrategy.getDescription());
            list.add(map);
        }

        return list;
    }
}
