package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.TaskDependVo;

/**
 * Created by hejian on 15/12/8.
 */
public interface TaskDependMapper {
    TaskDependVo getTaskDependByTaskId(Long taskId);

}
