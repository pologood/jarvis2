package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.web.entity.vo.JobSearchVo;
import com.mogujie.jarvis.web.entity.vo.JobVo;

import java.util.List;

/**
 * Created by hejian on 15/9/17.
 */
public interface JobMapper {
    public JobVo getJobById(Long jobId);
    public Integer getCountByCondition(JobSearchVo jobSearchVo);
    public List<JobVo> getJobsByCondition(JobSearchVo jobSearchVo);

    public List<Long> getJobIds();
    public List<String> getJobNames();
    public List<String> getSubmitUsers();
}
