package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.CronTabVo;
import com.mogujie.jarvis.web.entity.vo.JobQo;
import com.mogujie.jarvis.web.entity.vo.JobVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by hejian on 15/9/17.
 */
public interface JobMapper {
    JobVo getJobById(Long jobId);
    JobVo getJobByName(String jobName);
    Integer getCountByCondition(JobQo jobQo);
    List<JobVo> getJobsByCondition(JobQo jobQo);

    List<Long> getJobIds();
    List<String> getJobNames();
    List<String> getSubmitUsers();

    List<Long> getSimilarJobIds(Long jobId);
    List<String> getSimilarJobNames(String jobName);

    CronTabVo getCronTabByJobId(Long jobId);

    List<JobVo> getJobByIds(@Param("list")Set<String> jobIds);

}
