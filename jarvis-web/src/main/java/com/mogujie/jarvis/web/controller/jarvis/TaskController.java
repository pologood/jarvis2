package com.mogujie.jarvis.web.controller.jarvis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import com.mogujie.jarvis.web.entity.vo.JobVo;
import com.mogujie.jarvis.web.entity.vo.TaskSearchVo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
import com.mogujie.jarvis.web.service.JobService;
import com.mogujie.jarvis.web.service.TaskService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hejian on 15/9/14.
 */
@Controller
@RequestMapping("/jarvis/task")
public class TaskController extends BaseController {

    @Autowired
    JobService jobService;
    @Autowired
    TaskService taskService;

    Logger logger=Logger.getLogger(this.getClass());

    @RequestMapping
    @JarvisPassport(authTypes = JarvisAuthType.task)
    public String index(ModelMap modelMap){
        List<Long> jobIds= jobService.getJobIds();
        List<String> jobNames= jobService.getJobNames();
        List<String> submitUsers= jobService.getSubmitUsers();

        modelMap.put("jobIds",jobIds);
        modelMap.put("jobNames",jobNames);
        modelMap.put("submitUsers",submitUsers);

        return "task/index";
    }

    @RequestMapping(value = "detail")
    public String dependency(ModelMap modelMap,Long taskId){
        TaskVo taskVo=taskService.getTaskById(taskId);
        Long jobId=taskVo.getJobId();
        JobVo jobVo=jobService.getJobById(jobId);

        TaskSearchVo paraTaskVo=new TaskSearchVo();
        paraTaskVo.setJobId(jobId);
        paraTaskVo.setStatus(4);
        paraTaskVo.setOrderField("executeEndTime");
        paraTaskVo.setOrder("DESC");
        paraTaskVo.setOffset(0);
        paraTaskVo.setLimit(30);

        JSONObject jsonObject=taskService.getTasks(paraTaskVo);
        logger.info("json:"+jsonObject);
        JSONArray arr=jsonObject.getJSONArray("rows");
        List<TaskVo> taskVoList=new ArrayList<TaskVo>();
        if(arr!=null&&arr.size()>0){
            taskVoList=JSONArray.parseArray(arr.toJSONString(),TaskVo.class);
            Collections.reverse(taskVoList);

            Long avgTime=0l;
            Long totalTime=0l;
            for(TaskVo taskVo1:taskVoList){
                totalTime+=taskVo1.getExecuteTime();
            }
            avgTime=totalTime/taskVoList.size();
            taskVo.setAvgExecuteTime(avgTime);
        }

        logger.info("result:"+JSONArray.toJSONString(taskVoList));

        modelMap.put("taskVo", taskVo);
        modelMap.put("jobVo",jobVo);
        modelMap.put("taskVoList",JSONArray.toJSON(taskVoList).toString());

        return "task/detail";
    }
}
