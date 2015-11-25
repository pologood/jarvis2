package com.mogujie.jarvis.dao.generate;

import com.mogujie.jarvis.dto.generate.TaskSchedule;
import com.mogujie.jarvis.dto.generate.TaskScheduleExample;
import com.mogujie.jarvis.dto.generate.TaskScheduleKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TaskScheduleMapper {
    int countByExample(TaskScheduleExample example);

    int deleteByExample(TaskScheduleExample example);

    int deleteByPrimaryKey(TaskScheduleKey key);

    int insert(TaskSchedule record);

    int insertSelective(TaskSchedule record);

    List<TaskSchedule> selectByExample(TaskScheduleExample example);

    TaskSchedule selectByPrimaryKey(TaskScheduleKey key);

    int updateByExampleSelective(@Param("record") TaskSchedule record, @Param("example") TaskScheduleExample example);

    int updateByExample(@Param("record") TaskSchedule record, @Param("example") TaskScheduleExample example);

    int updateByPrimaryKeySelective(TaskSchedule record);

    int updateByPrimaryKey(TaskSchedule record);
}