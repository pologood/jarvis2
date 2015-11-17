package com.mogujie.jarvis.dao;

import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.dto.JobDependExample;
import com.mogujie.jarvis.dto.JobDependKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JobDependMapper {
    int countByExample(JobDependExample example);

    int deleteByExample(JobDependExample example);

    int deleteByPrimaryKey(JobDependKey key);

    int insert(JobDepend record);

    int insertSelective(JobDepend record);

    List<JobDepend> selectByExample(JobDependExample example);

    JobDepend selectByPrimaryKey(JobDependKey key);

    int updateByExampleSelective(@Param("record") JobDepend record, @Param("example") JobDependExample example);

    int updateByExample(@Param("record") JobDepend record, @Param("example") JobDependExample example);

    int updateByPrimaryKeySelective(JobDepend record);

    int updateByPrimaryKey(JobDepend record);
}