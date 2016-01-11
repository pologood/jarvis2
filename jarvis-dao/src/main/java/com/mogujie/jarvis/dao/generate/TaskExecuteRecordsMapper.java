package com.mogujie.jarvis.dao.generate;

import com.mogujie.jarvis.dto.generate.TaskExecuteRecords;
import com.mogujie.jarvis.dto.generate.TaskExecuteRecordsExample;
import com.mogujie.jarvis.dto.generate.TaskExecuteRecordsKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TaskExecuteRecordsMapper {
    int countByExample(TaskExecuteRecordsExample example);

    int deleteByExample(TaskExecuteRecordsExample example);

    int deleteByPrimaryKey(TaskExecuteRecordsKey key);

    int insert(TaskExecuteRecords record);

    int insertSelective(TaskExecuteRecords record);

    List<TaskExecuteRecords> selectByExampleWithBLOBs(TaskExecuteRecordsExample example);

    List<TaskExecuteRecords> selectByExample(TaskExecuteRecordsExample example);

    TaskExecuteRecords selectByPrimaryKey(TaskExecuteRecordsKey key);

    int updateByExampleSelective(@Param("record") TaskExecuteRecords record, @Param("example") TaskExecuteRecordsExample example);

    int updateByExampleWithBLOBs(@Param("record") TaskExecuteRecords record, @Param("example") TaskExecuteRecordsExample example);

    int updateByExample(@Param("record") TaskExecuteRecords record, @Param("example") TaskExecuteRecordsExample example);

    int updateByPrimaryKeySelective(TaskExecuteRecords record);

    int updateByPrimaryKeyWithBLOBs(TaskExecuteRecords record);

    int updateByPrimaryKey(TaskExecuteRecords record);
}