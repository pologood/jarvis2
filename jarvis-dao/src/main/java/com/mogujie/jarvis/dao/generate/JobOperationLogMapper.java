package com.mogujie.jarvis.dao.generate;

import com.mogujie.jarvis.dto.generate.JobOperationLog;
import com.mogujie.jarvis.dto.generate.JobOperationLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JobOperationLogMapper {
    int countByExample(JobOperationLogExample example);

    int deleteByExample(JobOperationLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(JobOperationLog record);

    int insertSelective(JobOperationLog record);

    java.util.List<com.mogujie.jarvis.dto.generate.JobOperationLog> selectByExample(JobOperationLogExample example);

    JobOperationLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") JobOperationLog record, @Param("example") JobOperationLogExample example);

    int updateByExample(@Param("record") JobOperationLog record, @Param("example") JobOperationLogExample example);

    int updateByPrimaryKeySelective(JobOperationLog record);

    int updateByPrimaryKey(JobOperationLog record);
}