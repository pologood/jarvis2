package com.mogujie.jarvis.web.service;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.web.common.Constants;
import com.mogujie.jarvis.web.common.TimeTools;
import com.mogujie.jarvis.web.entity.vo.CronTabVo;
import com.mogujie.jarvis.web.entity.vo.JobSearchVo;
import com.mogujie.jarvis.web.entity.vo.JobVo;
import com.mogujie.jarvis.web.mapper.JobMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by hejian on 15/9/17.
 */
@Service
public class JobService {
    @Autowired
    private JobMapper jobMapper;

    Logger logger = Logger.getLogger(this.getClass());

    public List<JobVo> getAllJobs(Integer jobFlag){
        JobSearchVo jobSearchVo=new JobSearchVo();
        jobSearchVo.setJobFlag(jobFlag);
        List<JobVo> jobVoList=jobMapper.getJobsByCondition(jobSearchVo);
        return jobVoList;
    }

    public JSONObject getJobs(JobSearchVo jobSearchVo){
        JSONObject jsonObject=new JSONObject();
        Integer count=jobMapper.getCountByCondition(jobSearchVo);
        count=count==null?0:count;

        List<JobVo> jobList=jobMapper.getJobsByCondition(jobSearchVo);

        logger.info("flagMap:"+Constants.jobFlagMap);
        for(JobVo jobVo:jobList){
            jobVo.setJobStatus(Constants.jobFlagMap.get(jobVo.getJobFlag()));
            jobVo.setJobPriority(Constants.jobPriorityMap.get(jobVo.getPriority()));
            jobVo.setCreateTimeStr(TimeTools.formatDateTime(jobVo.getCreateTime()));
            jobVo.setUpdateTimeStr(TimeTools.formatDateTime(jobVo.getUpdateTime()));
            jobVo.setActiveStartDateStr(TimeTools.formatDate(jobVo.getActiveStartDate()));
            jobVo.setActiveEndDateStr(TimeTools.formatDate(jobVo.getActiveEndDate()));
        }

        jsonObject.put("total",count);
        jsonObject.put("rows", jobList);

        return jsonObject;
    }

    public JobVo getJobById(Long jobId){
        return jobMapper.getJobById(jobId);
    }
    public JobVo getJobByName(String jobName){
        return jobMapper.getJobByName(jobName);
    }
    public List<Long> getJobIds(){
        return jobMapper.getJobIds();
    }
    public List<String> getJobNames(){
        return jobMapper.getJobNames();
    }
    public List<String> getSubmitUsers(){
        return jobMapper.getSubmitUsers();
    }

    public CronTabVo getCronTabByJobId(Long jobId){
        return jobMapper.getCronTabByJobId(jobId);
    }
}
