package com.mogujie.jarvis.dao.generate;

import org.apache.ibatis.annotations.Param;

import com.mogujie.jarvis.dto.generate.TaskDepend;
import com.mogujie.jarvis.dto.generate.TaskDependExample;

public interface TaskDependMapper {
    int countByExample(TaskDependExample example);

    int deleteByExample(TaskDependExample example);

    int deleteByPrimaryKey(Long taskId);

    int insert(TaskDepend record);

    int insertSelective(TaskDepend record);

    java.util.List<com.mogujie.jarvis.dto.generate.TaskDepend> selectByExample(TaskDependExample example);

    TaskDepend selectByPrimaryKey(Long taskId);

    int updateByExampleSelective(@Param("record") TaskDepend record, @Param("example") TaskDependExample example);

    int updateByExample(@Param("record") TaskDepend record, @Param("example") TaskDependExample example);

    int updateByPrimaryKeySelective(TaskDepend record);

    int updateByPrimaryKey(TaskDepend record);
}