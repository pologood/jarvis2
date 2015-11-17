package com.mogujie.jarvis.dao;

import com.mogujie.jarvis.dto.WorkerGroup;
import com.mogujie.jarvis.dto.WorkerGroupExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WorkerGroupMapper {
    int countByExample(WorkerGroupExample example);

    int deleteByExample(WorkerGroupExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(WorkerGroup record);

    int insertSelective(WorkerGroup record);

    List<WorkerGroup> selectByExample(WorkerGroupExample example);

    WorkerGroup selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") WorkerGroup record, @Param("example") WorkerGroupExample example);

    int updateByExample(@Param("record") WorkerGroup record, @Param("example") WorkerGroupExample example);

    int updateByPrimaryKeySelective(WorkerGroup record);

    int updateByPrimaryKey(WorkerGroup record);
}