package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.TaskSearchVo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;

import java.util.List;

/**
 * Created by hejian on 15/9/17.
 */
public interface TaskMapper {
    public TaskVo getTaskById(Long taskId);
    public Integer getCountByCondition(TaskSearchVo taskSearchVo);
    public List<TaskVo> getTasksByCondition(TaskSearchVo taskSearchVo);
}
