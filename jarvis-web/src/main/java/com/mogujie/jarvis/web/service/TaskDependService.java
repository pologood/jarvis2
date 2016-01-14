package com.mogujie.jarvis.web.service;

import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.web.entity.vo.*;
import com.mogujie.jarvis.web.mapper.*;
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
    @Autowired
    TaskHistoryMapper taskHistoryMapper;

    Logger logger = Logger.getLogger(TaskDependService.class);

    /*
    * 根据taskId获取task依赖信息
    * */
    public TaskDependVo getTaskDependByTaskId(Long taskId) {
        return taskDependMapper.getTaskDependByTaskId(taskId);
    }


    /**
     * 生成前置任务、后续任务的执行情况
     */
    public void generate(TaskDependVo taskDependVo) {
        TaskVo task = taskMapper.getTaskById(taskDependVo.getTaskId());   //根据依赖记录的taskId查询task详细信息
        JobVo job = jobMapper.getJobById(task.getJobId());                //根据task记录的jobId查询job详细信息
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

        String dependTaskIdsStr = taskDependVo.getDependTaskIds();      //获取task_depend的前置任务
        String childTaskIdsStr = taskDependVo.getChildTaskIds();        //获取task_depend的后续任务
        Map<String, List<Double>> previousTaskIds = JsonHelper.fromJson(dependTaskIdsStr, Map.class);  //解析json字符串
        Map<String, List<Double>> nextTaskIds = JsonHelper.fromJson(childTaskIdsStr, Map.class);       //解析json字符串


        //获取父所有job
        List<JobDependVo> parents = jobDependMapper.getParentById(task.getJobId());
        Set<String> previousJobIds = new HashSet<String>();
        for (JobDependVo jobDependVo : parents) {
            previousJobIds.add(jobDependVo.getId().toString());
        }
        //获取所有子job
        List<JobDependVo> children = jobDependMapper.getChildrenById(task.getJobId());
        Set<String> nextJobIds = new HashSet<String>();
        for (JobDependVo jobDependVo : children) {
            nextJobIds.add(jobDependVo.getId().toString());
        }
        List<JobVo> preJobList = new ArrayList<JobVo>();
        if (previousJobIds.size() > 0) {
            preJobList = jobMapper.getJobByIds(previousJobIds);      //批量查询，提高效率
        }

        List<JobVo> nextJobList = new ArrayList<JobVo>();
        if (nextJobIds.size() > 0) {
            nextJobList = jobMapper.getJobByIds(nextJobIds);
        }

        //所有的taskId
        Set<String> taskIds = new HashSet<String>();
        for (Object object : previousTaskIds.entrySet()) {
            Map.Entry entry = (Map.Entry) object;
            List<Double> list = (List) entry.getValue();
            for (int i = 0, size = list.size(); i < size; i++) {
                taskIds.add(String.valueOf(list.get(i).longValue()));
            }
        }
        for (Object object : nextTaskIds.entrySet()) {
            Map.Entry entry = (Map.Entry) object;
            List<Double> list = (List) entry.getValue();
            for (int i = 0, size = list.size(); i < size; i++) {
                taskIds.add(String.valueOf(list.get(i).longValue()));
            }
        }
        logger.info("获取task");
        List<TaskVo> taskList = taskMapper.getTaskByIds(taskIds);

        Map<Long, JobVo> jobMap = new HashMap<Long, JobVo>();
        Map<Long, TaskVo> taskMap = new HashMap<Long, TaskVo>();

        //构造map，方便后续使用
        for (JobVo jobVo : preJobList) {
            jobMap.put(jobVo.getJobId(), jobVo);
        }
        for (JobVo jobVo : nextJobList) {
            jobMap.put(jobVo.getJobId(), jobVo);
        }
        for (TaskVo taskVo : taskList) {
            taskMap.put(taskVo.getTaskId(), taskVo);
        }

        //构造前置任务的执行详情
        generateJobDetail(taskDependVo, preJobList, previousTaskIds, taskMap, true);
        //构造后续任务的执行详情
        generateJobDetail(taskDependVo, nextJobList, nextTaskIds, taskMap, false);
    }

    /*
    * 构造父节点或者子节点的执行详情
    * */
    public void generateJobDetail(TaskDependVo taskDependVo, List<JobVo> jobVoList, Map<String, List<Double>> taskIds, Map<Long, TaskVo> taskMap, boolean isParent) {
        for (JobVo jobVo : jobVoList) {
            TaskDependVo singleTaskDependVo = new TaskDependVo();

            List<Double> list = taskIds.get(jobVo.getJobId().toString());
            if (list == null) {
                list = new ArrayList<Double>();
            }

            singleTaskDependVo.setJobId(jobVo.getJobId());
            singleTaskDependVo.setJobName(jobVo.getJobName());
            singleTaskDependVo.setTotalTask(list.size());


            Integer completeCount = 0;
            //只有有效状态才设置taskList
            if (jobVo.getStatus().equals(1)) {
                for (int i = 0, size = list.size(); i < size; i++) {
                    Long taskId = list.get(i).longValue();
                    TaskVo singleTaskVo = taskMap.get(taskId);


                    if (null != singleTaskVo) {
                        singleTaskDependVo.getTaskList().add(singleTaskVo);
                        //4代表success
                        if (singleTaskVo.getStatus().equals(4)) {
                            completeCount++;
                        }
                    }
                }
            } else {
                singleTaskDependVo.setStatus(91);     //91代表失效、过期、垃圾箱等(在前台渲染需要)
            }
            singleTaskDependVo.setCompleteTask(completeCount);
            singleTaskDependVo.setParentFlag(isParent);
            if (isParent) {
                taskDependVo.getParents().add(singleTaskDependVo);
            } else {
                taskDependVo.getChildren().add(singleTaskDependVo);
            }
        }
    }

}
