package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.TaskExecuteRecordsVo;

import java.util.List;

/**
 * Created by hejian on 16/1/11.
 */
public interface TaskExecuteRecordsMapper {
    List<TaskExecuteRecordsVo> getByTaskId(Long taskId);
}
