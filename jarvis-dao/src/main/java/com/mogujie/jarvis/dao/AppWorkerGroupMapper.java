package com.mogujie.jarvis.dao;

import com.mogujie.jarvis.dto.AppWorkerGroup;
import com.mogujie.jarvis.dto.AppWorkerGroupExample;
import com.mogujie.jarvis.dto.AppWorkerGroupKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppWorkerGroupMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_worker_group
     *
     * @mbggenerated Mon Nov 16 16:37:20 CST 2015
     */
    int countByExample(AppWorkerGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_worker_group
     *
     * @mbggenerated Mon Nov 16 16:37:20 CST 2015
     */
    int deleteByExample(AppWorkerGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_worker_group
     *
     * @mbggenerated Mon Nov 16 16:37:20 CST 2015
     */
    int deleteByPrimaryKey(AppWorkerGroupKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_worker_group
     *
     * @mbggenerated Mon Nov 16 16:37:20 CST 2015
     */
    int insert(AppWorkerGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_worker_group
     *
     * @mbggenerated Mon Nov 16 16:37:20 CST 2015
     */
    int insertSelective(AppWorkerGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_worker_group
     *
     * @mbggenerated Mon Nov 16 16:37:20 CST 2015
     */
    List<AppWorkerGroup> selectByExample(AppWorkerGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_worker_group
     *
     * @mbggenerated Mon Nov 16 16:37:20 CST 2015
     */
    AppWorkerGroup selectByPrimaryKey(AppWorkerGroupKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_worker_group
     *
     * @mbggenerated Mon Nov 16 16:37:20 CST 2015
     */
    int updateByExampleSelective(@Param("record") AppWorkerGroup record, @Param("example") AppWorkerGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_worker_group
     *
     * @mbggenerated Mon Nov 16 16:37:20 CST 2015
     */
    int updateByExample(@Param("record") AppWorkerGroup record, @Param("example") AppWorkerGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_worker_group
     *
     * @mbggenerated Mon Nov 16 16:37:20 CST 2015
     */
    int updateByPrimaryKeySelective(AppWorkerGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_worker_group
     *
     * @mbggenerated Mon Nov 16 16:37:20 CST 2015
     */
    int updateByPrimaryKey(AppWorkerGroup record);
}