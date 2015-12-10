package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.JobDependVo;

import java.util.List;

/**
 * Created by hejian on 15/9/22.
 */
public interface JobDependMapper {
    JobDependVo getJobById(Long jobId);
    List<JobDependVo> getChildrenById(Long jobId);
    List<JobDependVo> getParentById(Long jobId);
}
