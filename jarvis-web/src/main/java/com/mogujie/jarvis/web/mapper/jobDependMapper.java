package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.JobDependVo;

import java.util.List;

/**
 * Created by hejian on 15/9/22.
 */
public interface JobDependMapper {
    public JobDependVo getJobById(Long jobId); 
    public List<JobDependVo> getChildrenById(Long jobId);
}
