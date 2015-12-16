package com.mogujie.jarvis.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.web.entity.vo.JobDependVo;
import com.mogujie.jarvis.web.entity.vo.JobVo;
import com.mogujie.jarvis.web.entity.vo.TaskDependVo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
import com.mogujie.jarvis.web.mapper.JobDependMapper;
import com.mogujie.jarvis.web.mapper.JobMapper;
import com.mogujie.jarvis.web.mapper.TaskDependMapper;
import com.mogujie.jarvis.web.mapper.TaskMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by hejian on 15/12/8.
 */
@Service
public class TaskDependService {
    @Autowired
    TaskDependMapper taskDependMapper;
    @Autowired
    JobMapper jobMapper;
    @Autowired
    JobDependMapper jobDependMapper;
    @Autowired
    TaskMapper taskMapper;

    Logger logger=Logger.getLogger(TaskDependService.class);

    /*
    *
    * */
    public TaskDependVo getTaskDependByTaskId(Long taskId){
        return taskDependMapper.getTaskDependByTaskId(taskId);
    }


    /**
     * 生成前置任务、后续任务的执行情况
     * */
    public void generate(TaskDependVo taskDependVo){
        TaskVo task=taskMapper.getTaskById(taskDependVo.getTaskId());
        JobVo job =jobMapper.getJobById(task.getJobId());
        //初始化当前task基本情况
        taskDependVo.setRootFlag(true);
        taskDependVo.setExecuteUser(task.getExecuteUser());
        taskDependVo.setJobId(task.getJobId());
        taskDependVo.setJobName(job.getJobName());
        taskDependVo.setStatus(task.getStatus());
        taskDependVo.setScheduleTime(task.getScheduleTime());
        taskDependVo.setExecuteStartTime(task.getExecuteStartTime());
        taskDependVo.setExecuteEndTime(task.getExecuteEndTime());
        taskDependVo.setExecuteTime(task.getExecuteTime());
        //


        String dependTaskIdsStr=taskDependVo.getDependTaskIds();
        String childTaskIdsStr=taskDependVo.getChildTaskIds();

        logger.info("dependTaskIdsStr:"+dependTaskIdsStr);
        logger.info("childTaskIdsStr:"+childTaskIdsStr);
        JSONObject dependTaskIds = JSON.parseObject(dependTaskIdsStr);
        JSONObject childTaskIds = JSON.parseObject(childTaskIdsStr);

        Set<String> dependJobIds=dependTaskIds.keySet();
        Set<String> childJobIds=childTaskIds.keySet();

        List<JobDependVo> jobDependVoList=jobDependMapper.getParentById(task.getJobId());
        Set<String> PreJobIds=new HashSet<String>();
        for(JobDependVo jobDependVo:jobDependVoList){
            PreJobIds.add(jobDependVo.getId().toString());
        }


        //所有的job信息
        Set<String> preJobIds=new HashSet<String>();
        Set<String> postJobIds=new HashSet<String>();
        preJobIds.addAll(PreJobIds);    //job表中的前置(有些过期或失效的job不会存在task依赖表中)
        postJobIds.addAll(childJobIds);  //task依赖表中的后续


        //所有的taskId
        Set<String> taskIds=new HashSet<String>();
        for(Map.Entry entry:dependTaskIds.entrySet()){
            JSONArray jsonArray=(JSONArray)entry.getValue();
            for(int i=0,size=jsonArray.size();i<size;i++){
                taskIds.add(jsonArray.getString(i));
            }
        }
        for(Map.Entry entry:childTaskIds.entrySet()){
            JSONArray jsonArray=(JSONArray)entry.getValue();
            for(int i=0,size=jsonArray.size();i<size;i++){
                taskIds.add(jsonArray.getString(i));
            }
        }

        //批量查询，提高效率
        logger.info("获取job");
        List<JobVo> preJobList= new ArrayList<JobVo>();
        if(preJobIds.size()>0){
            preJobList=jobMapper.getJobByIds(preJobIds);
        }

        List<JobVo> postJobList=new ArrayList<JobVo>();
        if(postJobIds.size()>0){
            postJobList=jobMapper.getJobByIds(postJobIds);
        }

        logger.info("获取task");
        List<TaskVo> taskList=taskMapper.getTaskByIds(taskIds);

        Map<Long,JobVo> jobMap=new HashMap<Long, JobVo>();
        Map<Long,TaskVo> taskMap=new HashMap<Long, TaskVo>();

        //构造map，方便后续使用
        for(JobVo jobVo:postJobList){
            jobMap.put(jobVo.getJobId(),jobVo);
        }
        for(TaskVo taskVo:taskList){
            taskMap.put(taskVo.getTaskId(), taskVo);
        }

        //构造前置任务的执行详情
        for(JobVo jobVo:preJobList){

            TaskDependVo singleTaskDependVo = new TaskDependVo();
            JSONArray jsonArray=dependTaskIds.getJSONArray(jobVo.getJobId().toString());
            if(jsonArray==null){
                jsonArray=new JSONArray();
            }


            singleTaskDependVo.setJobId(jobVo.getJobId());
            singleTaskDependVo.setJobName(jobVo.getJobName());
            singleTaskDependVo.setTotalTask(jsonArray.size());

            Integer completeCount=0;
            //只有有效状态才设置taskList
            if(jobVo.getStatus().equals(1)){
                for(int i=0,size=jsonArray.size();i<size;i++){
                    TaskVo singleTaskVo=taskMap.get(jsonArray.getLong(i));
                    singleTaskDependVo.getTaskList().add(singleTaskVo);
                    //4代表success
                    if(singleTaskVo.getStatus().equals(4)){
                        completeCount++;
                    }
                }
            }
            else{
                singleTaskDependVo.setStatus(91);     //91代表失效、过期、垃圾箱等，在前台渲染需要
            }
            singleTaskDependVo.setCompleteTask(completeCount);
            singleTaskDependVo.setParentFlag(true);

            taskDependVo.getParents().add(singleTaskDependVo);

        }

        //构造后续任务的执行详情
        for(Map.Entry entry:childTaskIds.entrySet()){
            JSONArray jsonArray=(JSONArray)entry.getValue();

            TaskDependVo singleTaskDependVo = new TaskDependVo();

            singleTaskDependVo.setJobId(Long.valueOf((String) entry.getKey()));
            singleTaskDependVo.setJobName(jobMap.get(Long.valueOf((String) entry.getKey())).getJobName());
            singleTaskDependVo.setTotalTask(jsonArray.size());

            Integer completeCount=0;
            for(int i=0,size=jsonArray.size();i<size;i++){
                TaskVo singleTaskVo=taskMap.get(jsonArray.getLong(i));
                singleTaskDependVo.getTaskList().add(singleTaskVo);
                if(singleTaskVo.getStatus().equals(4)){
                    completeCount++;
                }
            }
            singleTaskDependVo.setCompleteTask(completeCount);

            taskDependVo.getChildren().add(singleTaskDependVo);
        }

    }
}
