package com.mogujie.jarvis.dao.generate;

import com.mogujie.jarvis.dto.generate.JobScheduleExpression;
import com.mogujie.jarvis.dto.generate.JobScheduleExpressionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JobScheduleExpressionMapper {
    int countByExample(JobScheduleExpressionExample example);

    int deleteByExample(JobScheduleExpressionExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(JobScheduleExpression record);

    int insertSelective(JobScheduleExpression record);

    List<JobScheduleExpression> selectByExample(JobScheduleExpressionExample example);

    JobScheduleExpression selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") JobScheduleExpression record, @Param("example") JobScheduleExpressionExample example);

    int updateByExample(@Param("record") JobScheduleExpression record, @Param("example") JobScheduleExpressionExample example);

    int updateByPrimaryKeySelective(JobScheduleExpression record);

    int updateByPrimaryKey(JobScheduleExpression record);
}