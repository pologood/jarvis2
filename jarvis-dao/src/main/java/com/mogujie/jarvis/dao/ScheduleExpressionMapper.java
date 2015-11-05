package com.mogujie.jarvis.dao;

import com.mogujie.jarvis.dto.ScheduleExpression;
import com.mogujie.jarvis.dto.ScheduleExpressionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ScheduleExpressionMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table schedule_expression
     *
     * @mbggenerated Thu Nov 05 20:32:54 CST 2015
     */
    int countByExample(ScheduleExpressionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table schedule_expression
     *
     * @mbggenerated Thu Nov 05 20:32:54 CST 2015
     */
    int deleteByExample(ScheduleExpressionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table schedule_expression
     *
     * @mbggenerated Thu Nov 05 20:32:54 CST 2015
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table schedule_expression
     *
     * @mbggenerated Thu Nov 05 20:32:54 CST 2015
     */
    int insert(ScheduleExpression record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table schedule_expression
     *
     * @mbggenerated Thu Nov 05 20:32:54 CST 2015
     */
    int insertSelective(ScheduleExpression record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table schedule_expression
     *
     * @mbggenerated Thu Nov 05 20:32:54 CST 2015
     */
    List<ScheduleExpression> selectByExample(ScheduleExpressionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table schedule_expression
     *
     * @mbggenerated Thu Nov 05 20:32:54 CST 2015
     */
    ScheduleExpression selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table schedule_expression
     *
     * @mbggenerated Thu Nov 05 20:32:54 CST 2015
     */
    int updateByExampleSelective(@Param("record") ScheduleExpression record, @Param("example") ScheduleExpressionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table schedule_expression
     *
     * @mbggenerated Thu Nov 05 20:32:54 CST 2015
     */
    int updateByExample(@Param("record") ScheduleExpression record, @Param("example") ScheduleExpressionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table schedule_expression
     *
     * @mbggenerated Thu Nov 05 20:32:54 CST 2015
     */
    int updateByPrimaryKeySelective(ScheduleExpression record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table schedule_expression
     *
     * @mbggenerated Thu Nov 05 20:32:54 CST 2015
     */
    int updateByPrimaryKey(ScheduleExpression record);
}