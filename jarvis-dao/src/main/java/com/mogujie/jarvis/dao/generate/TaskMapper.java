package com.mogujie.jarvis.dao.generate;

import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.dto.generate.TaskExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TaskMapper {
    int countByExample(TaskExample example);

    int deleteByExample(TaskExample example);

    int deleteByPrimaryKey(Long taskId);

    int insert(Task record);

    int insertSelective(Task record);

    List<Task> selectByExampleWithBLOBs(TaskExample example);

    List<Task> selectByExample(TaskExample example);

    Task selectByPrimaryKey(Long taskId);

    int updateByExampleSelective(@Param("record") Task record, @Param("example") TaskExample example);

    int updateByExampleWithBLOBs(@Param("record") Task record, @Param("example") TaskExample example);

    int updateByExample(@Param("record") Task record, @Param("example") TaskExample example);

    int updateByPrimaryKeySelective(Task record);

    int updateByPrimaryKeyWithBLOBs(Task record);

    int updateByPrimaryKey(Task record);
}