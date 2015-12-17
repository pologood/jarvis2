package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.qo.PlanQo;
import com.mogujie.jarvis.web.entity.vo.PlanVo;
import com.mogujie.jarvis.web.entity.qo.TaskQo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by hejian on 15/9/17.
 */
public interface TaskMapper {

    TaskVo getTaskById(Long taskId);

    Integer getCountByCondition(TaskQo taskQo);

    List<TaskVo> getTasksByCondition(TaskQo taskQo);

    List<String> getAllExecuteUser();

    /**
     * 执行计划
     */

    Integer getPlanCountByCondition(PlanQo planQo);      //某个

    List<PlanVo> getPlansByCondition(PlanQo planQo);


    List<TaskVo> getTaskByIds(@Param("list") Set<String> taskIds);
}
