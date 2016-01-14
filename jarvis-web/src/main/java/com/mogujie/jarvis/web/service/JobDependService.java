package com.mogujie.jarvis.web.service;

import com.mogujie.jarvis.web.entity.vo.JobDependVo;
import com.mogujie.jarvis.web.mapper.JobDependMapper;
import com.mogujie.jarvis.web.entity.qo.JobQo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hejian on 15/9/22.
 */
@Service
public class JobDependService {
    @Autowired
    JobDependMapper jobDependMapper;

    /**
     * 获取所有依赖于此job的job
     */
    public Map<String, Object> getTreeDependedOnJob(JobQo jobQo) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (null == jobQo || null == jobQo.getJobId()) {
            return result;
        }
        JobDependVo jobDependVo = jobDependMapper.getJobById(Long.valueOf(jobQo.getJobId()));

        if (null == jobDependVo) {
            return result;
        }
        Map<String, Object> mapState = new HashMap<String, Object>();
        mapState.put("opened", true);

        List<JobDependVo> jobDependVoChildrenList = getChildren(jobDependVo, true);
        jobDependVo.setChildren(jobDependVoChildrenList);

        try {
            Field[] fields = jobDependVo.getClass().getDeclaredFields();
            for (int i = 0, len = fields.length; i < len; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = field.get(jobDependVo);
                result.put(field.getName(), value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.put("state", mapState);

        return result;
    }

    /**
     * 获取所有依赖于此job的job
     */
    public Map<String, Object> getTwoDirectionTreeDependedOnJob(JobQo jobQo) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (null == jobQo || null == jobQo.getJobId()) {
            return result;
        }

        JobDependVo jobDependVo = jobDependMapper.getJobById(jobQo.getJobId());
        jobDependVo.setName(jobDependVo.getText());
        jobDependVo.setValue(jobDependVo.getId());
        jobDependVo.setRootFlag(true);

        if (null == jobDependVo) {
            return result;
        }
        Map<String, Object> mapState = new HashMap<String, Object>();
        mapState.put("opened", true);

        List<JobDependVo> jobDependVoChildrenList = getChildren(jobDependVo, false);
        jobDependVo.setChildren(jobDependVoChildrenList);


        List<JobDependVo> jobDependVoParentList = getParents(jobDependVo, false);
        jobDependVo.setParents(jobDependVoParentList);

        try {
            Field[] fields = jobDependVo.getClass().getDeclaredFields();
            for (int i = 0, len = fields.length; i < len; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Object value = field.get(jobDependVo);
                result.put(field.getName(), value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.put("state", mapState);

        return result;
    }

    /**
     * 递归获取所有子节点
     */
    public List<JobDependVo> getChildren(JobDependVo jobDependVo, boolean all) {
        List<JobDependVo> jobChildren = jobDependMapper.getChildrenById(jobDependVo.getId());
        Map<String, Object> mapState = new HashMap<String, Object>();
        mapState.put("opened", true);

        if (jobChildren != null && jobChildren.size() > 0) {
            for (JobDependVo childJob : jobChildren) {
                childJob.setState(mapState);
                childJob.setName(childJob.getText());
                childJob.setValue(childJob.getId());
                if (all) {
                    childJob.setChildren(getChildren(childJob, all));
                }
            }
        }

        return jobChildren;
    }

    /**
     * 递归获取所有父节点
     */
    public List<JobDependVo> getParents(JobDependVo jobDependVo, boolean all) {
        List<JobDependVo> jobParents = jobDependMapper.getParentById(jobDependVo.getId());
        Map<String, Object> mapState = new HashMap<String, Object>();
        mapState.put("opened", true);
        if (jobParents != null && jobParents.size() > 0) {
            for (JobDependVo parentJob : jobParents) {
                parentJob.setState(mapState);
                parentJob.setParentFlag(true);
                parentJob.setName(parentJob.getText());
                parentJob.setValue(parentJob.getId());
                if (all) {
                    parentJob.setParents(getParents(parentJob, all));
                }
            }
        }

        return jobParents;
    }

    /**
     * 获取最近父节点
     */
    public List<JobDependVo> getParentById(Long jobId) {
        return jobDependMapper.getParentById(jobId);
    }
}
