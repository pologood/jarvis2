package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.TaskDependQo;
import com.mogujie.jarvis.web.entity.vo.TaskDependVo;

import java.util.List;

/**
 * Created by hejian on 15/12/8.
 */
public interface TaskDependMapper {
    TaskDependVo getTaskDependByTaskId(Long taskId);

}
