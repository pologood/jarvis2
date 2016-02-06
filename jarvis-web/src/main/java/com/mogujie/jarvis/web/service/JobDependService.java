
package com.mogujie.jarvis.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mogujie.jarvis.web.entity.vo.JobDependVo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
import com.mogujie.jarvis.web.entity.qo.JobDependQo;
import com.mogujie.jarvis.web.mapper.JobDependMapper;


/**
 * @author muming, hejian
 */
@Service
public class JobDependService {
    @Autowired
    JobDependMapper jobDependMapper;

    @Autowired
    TaskService taskService;

    private static Logger logger = LogManager.getLogger();

    /**
     * 获取最近父节点
     */
    public List<JobDependVo> getParentById(Long jobId) {
        return jobDependMapper.getParentById(jobId);
    }

    /**
     * 获取——自身与所有子节点.
     */
    public JobDependVo getSubTree(JobDependQo query) {
        if (null == query || 0 == query.getJobId()) {
            return null;
        }
        JobDependVo jobDependVo = jobDependMapper.getJobById(query.getJobId());
        if (null == jobDependVo) {
            return null;
        }
        jobDependVo.setRootFlag(true);

        List<JobDependVo> jobDependVoChildrenList = getChildren(jobDependVo, true);
        jobDependVo.setChildren(jobDependVoChildrenList);

        return jobDependVo;

    }

    /**
     * 获取——依赖(自身,父节点,子节点,共三层节点)
     */
    public JobDependVo getDepended(JobDependQo query) {
        if (null == query || 0 == query.getJobId()) {
            return null;
        }
        JobDependVo jobDependVo = jobDependMapper.getJobById(query.getJobId());
        if (null == jobDependVo) {
            return null;
        }
        jobDependVo.setRootFlag(true);

        List<JobDependVo> jobDependVoParentList = getParents(jobDependVo, false);
        jobDependVo.setParents(jobDependVoParentList);

        List<JobDependVo> jobDependVoChildrenList = getChildren(jobDependVo, false);
        jobDependVo.setChildren(jobDependVoChildrenList);

        generateTaskList4Depend(jobDependVo, query);

        return jobDependVo;

    }

    /**
     * 递归获取所有子节点
     */
    private List<JobDependVo> getChildren(JobDependVo jobDependVo, boolean all) {
        List<JobDependVo> jobChildren = jobDependMapper.getChildrenById(jobDependVo.getJobId());
        if (jobChildren == null) {
            jobChildren = new ArrayList<>();
        }
        for (JobDependVo childJob : jobChildren) {
            if (all) {
                childJob.setChildren(getChildren(childJob, all));
            }
        }
        return jobChildren;
    }

    /**
     * 递归获取所有父节点
     */
    private List<JobDependVo> getParents(JobDependVo jobDependVo, boolean all) {
        List<JobDependVo> jobParents = jobDependMapper.getParentById(jobDependVo.getJobId());
        if (jobParents == null) {
            jobParents = new ArrayList<>();
        }
        for (JobDependVo parentJob : jobParents) {
            parentJob.setParentFlag(true);
            if (all) {
                parentJob.setParents(getParents(parentJob, all));
            }
        }

        return jobParents;
    }

    /**
     * 获取——TaskList
     */
    private void generateTaskList4Depend(JobDependVo jobRoot, JobDependQo query) {

        //做成JobIds
        List<Long> jobIds = new ArrayList<>();
        jobIds.add(jobRoot.getJobId());
        for (JobDependVo parent : jobRoot.getParents()) {
            jobIds.add(parent.getJobId());
        }
        for (JobDependVo child : jobRoot.getChildren()) {
            jobIds.add(child.getJobId());
        }

        //获取taskList
        List<TaskVo> taskList = null;
        try {
            taskList = taskService.getTaskByJobIdBetweenTime(jobIds, query.getShowTaskStartTime(), query.getShowTaskEndTime());
        } catch (Exception ex) {
            logger.error("", ex);
        }
        if (taskList == null || taskList.size() == 0) {
            return;
        }
        Map<Long, List<TaskVo>> taskMap = new HashMap<>();
        for (TaskVo item : taskList) {
            Long jobId = item.getJobId();
            List<TaskVo> value;
            if (taskMap.containsKey(jobId)) {
                value = taskMap.get(jobId);
            } else {
                value = new ArrayList<>();
                taskMap.put(jobId, value);
            }
            value.add(item);
        }

        //设置——taskList
        long findJobId = jobRoot.getJobId();
        if (taskMap.containsKey(findJobId)) {
            jobRoot.setTaskList(taskMap.get(findJobId));
        }
        for (JobDependVo parent : jobRoot.getParents()) {
            findJobId = parent.getJobId();
            if (taskMap.containsKey(findJobId)) {
                parent.setTaskList(taskMap.get(findJobId));
            }
        }
        for (JobDependVo child : jobRoot.getChildren()) {
            findJobId = child.getJobId();
            if (taskMap.containsKey(findJobId)) {
                child.setTaskList(taskMap.get(findJobId));
            }
        }
    }

}
