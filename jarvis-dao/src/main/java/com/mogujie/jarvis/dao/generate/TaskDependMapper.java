package com.mogujie.jarvis.dao.generate;

import com.mogujie.jarvis.dto.generate.TaskDepend;
import com.mogujie.jarvis.dto.generate.TaskDependExample;
import com.mogujie.jarvis.dto.generate.TaskDependKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TaskDependMapper {
    int countByExample(TaskDependExample example);

    int deleteByExample(TaskDependExample example);

    int deleteByPrimaryKey(TaskDependKey key);

    int insert(TaskDepend record);

    int insertSelective(TaskDepend record);

    List<TaskDepend> selectByExample(TaskDependExample example);

    TaskDepend selectByPrimaryKey(TaskDependKey key);

    int updateByExampleSelective(@Param("record") TaskDepend record, @Param("example") TaskDependExample example);

    int updateByExample(@Param("record") TaskDepend record, @Param("example") TaskDependExample example);

    int updateByPrimaryKeySelective(TaskDepend record);

    int updateByPrimaryKey(TaskDepend record);
}