package com.mogujie.jarvis.dao;

import com.mogujie.jarvis.dto.JobScheduleExpression;
import com.mogujie.jarvis.dto.JobScheduleExpressionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JobScheduleExpressionMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_schedule_expression
     *
     * @mbggenerated Mon Nov 16 16:37:21 CST 2015
     */
    int countByExample(JobScheduleExpressionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_schedule_expression
     *
     * @mbggenerated Mon Nov 16 16:37:21 CST 2015
     */
    int deleteByExample(JobScheduleExpressionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_schedule_expression
     *
     * @mbggenerated Mon Nov 16 16:37:21 CST 2015
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_schedule_expression
     *
     * @mbggenerated Mon Nov 16 16:37:21 CST 2015
     */
    int insert(JobScheduleExpression record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_schedule_expression
     *
     * @mbggenerated Mon Nov 16 16:37:21 CST 2015
     */
    int insertSelective(JobScheduleExpression record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_schedule_expression
     *
     * @mbggenerated Mon Nov 16 16:37:21 CST 2015
     */
    List<JobScheduleExpression> selectByExample(JobScheduleExpressionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_schedule_expression
     *
     * @mbggenerated Mon Nov 16 16:37:21 CST 2015
     */
    JobScheduleExpression selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_schedule_expression
     *
     * @mbggenerated Mon Nov 16 16:37:21 CST 2015
     */
    int updateByExampleSelective(@Param("record") JobScheduleExpression record, @Param("example") JobScheduleExpressionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_schedule_expression
     *
     * @mbggenerated Mon Nov 16 16:37:21 CST 2015
     */
    int updateByExample(@Param("record") JobScheduleExpression record, @Param("example") JobScheduleExpressionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_schedule_expression
     *
     * @mbggenerated Mon Nov 16 16:37:21 CST 2015
     */
    int updateByPrimaryKeySelective(JobScheduleExpression record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table job_schedule_expression
     *
     * @mbggenerated Mon Nov 16 16:37:21 CST 2015
     */
    int updateByPrimaryKey(JobScheduleExpression record);
}