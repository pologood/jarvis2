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
    TaskHistoryMapper taskExecuteRecordsMapper;

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
        TaskVo task = taskMapper.getTaskById(taskDependVo.getTaskId());
        JobVo job = jobMapper.getJobById(task.getJobId());
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

        //获取任务依赖中的前置任务
        String dependTaskIdsStr = taskDependVo.getDependTaskIds();
        //获取任务依赖中的后续任务
        String childTaskIdsStr = taskDependVo.getChildTaskIds();

        logger.info("dependTaskIdsStr:" + dependTaskIdsStr);
        logger.info("childTaskIdsStr:" + childTaskIdsStr);
        //
        Map<String, List<Double>> previousTaskIds = JsonHelper.fromJson(dependTaskIdsStr, Map.class);
        Map<String, List<Double>> nextTaskIds = JsonHelper.fromJson(childTaskIdsStr, Map.class);

        Set<String> childJobIds = nextTaskIds.keySet();

        List<JobDependVo> jobDependVoList = jobDependMapper.getParentById(task.getJobId());
        Set<String> PreJobIds = new HashSet<String>();
        for (JobDependVo jobDependVo : jobDependVoList) {
            PreJobIds.add(jobDependVo.getId().toString());
        }


        //所有的job信息
        Set<String> previousJobIds = new HashSet<String>();
        Set<String> nextJobIds = new HashSet<String>();
        previousJobIds.addAll(PreJobIds);            //job表中的前置(有些过期或失效的job不会存在task依赖表中)
        nextJobIds.addAll(childJobIds);              //task依赖表中的后续


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

        //批量查询，提高效率
        logger.info("获取job");
        List<JobVo> preJobList = new ArrayList<JobVo>();
        logger.info("previousJobIds:" + previousJobIds);
        if (previousJobIds.size() > 0) {
            preJobList = jobMapper.getJobByIds(previousJobIds);
        }

        List<JobVo> nextJobList = new ArrayList<JobVo>();
        if (nextJobIds.size() > 0) {
            nextJobList = jobMapper.getJobByIds(nextJobIds);
        }

        logger.info("获取task");
        List<TaskVo> taskList = taskMapper.getTaskByIds(taskIds);

        Map<Long, JobVo> jobMap = new HashMap<Long, JobVo>();
        Map<Long, TaskVo> taskMap = new HashMap<Long, TaskVo>();

        //构造map，方便后续使用
        for (JobVo jobVo : nextJobList) {
            jobMap.put(jobVo.getJobId(), jobVo);
        }
        for (TaskVo taskVo : taskList) {
            taskMap.put(taskVo.getTaskId(), taskVo);
        }

        //构造前置任务的执行详情
        logger.info("previousTaskIds:" + previousTaskIds);
        for (JobVo jobVo : preJobList) {
            TaskDependVo singleTaskDependVo = new TaskDependVo();

            List<Double> list = previousTaskIds.get(jobVo.getJobId().toString());
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
            singleTaskDependVo.setParentFlag(true);

            taskDependVo.getParents().add(singleTaskDependVo);

        }

        //构造后续任务的执行详情
        for (Object object : nextTaskIds.entrySet()) {
            Map.Entry entry = (Map.Entry) object;

            List<Double> list = (List<Double>) entry.getValue();
            if (list == null) {
                list = new ArrayList<Double>();
            }
            TaskDependVo singleTaskDependVo = new TaskDependVo();

            singleTaskDependVo.setJobId(Long.valueOf((String) entry.getKey()));
            singleTaskDependVo.setJobName(jobMap.get(Long.valueOf((String) entry.getKey())).getJobName());
            singleTaskDependVo.setTotalTask(list.size());

            Integer completeCount = 0;
            for (int i = 0, size = list.size(); i < size; i++) {
                TaskVo singleTaskVo = taskMap.get(list.get(i).longValue());
                if (null != singleTaskVo) {
                    singleTaskDependVo.getTaskList().add(singleTaskVo);
                    if (singleTaskVo.getStatus().equals(4)) {
                        completeCount++;
                    }
                }
            }
            singleTaskDependVo.setCompleteTask(completeCount);

            taskDependVo.getChildren().add(singleTaskDependVo);
        }

    }
}
