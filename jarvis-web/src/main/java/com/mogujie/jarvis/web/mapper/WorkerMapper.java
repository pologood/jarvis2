package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.WorkerGroupQo;
import com.mogujie.jarvis.web.entity.vo.WorkerGroupVo;
import com.mogujie.jarvis.web.entity.vo.WorkerQo;
import com.mogujie.jarvis.web.entity.vo.WorkerVo;

import java.util.List;
import java.util.Map;

/**
 * Created by hejian on 15/9/28.
 */
public interface WorkerMapper {
    //worker
    WorkerVo getWorkerById(Integer id);
    Integer getWorkerCount(WorkerQo workerSearchVo);
    List<WorkerVo> getWorkerList(WorkerQo workerSearchVo);
    List<String> getAllWorkerIp();
    List<Integer> getAllWorkerPort();
    WorkerVo getWorkerByIpAndPort(Map<String,Object> para);


    //WorkerGroup
    List<WorkerGroupVo> getAllWorkerGroup();
    WorkerGroupVo getWorkerGroupById(Integer id);
    WorkerGroupVo getWorkerGroupByName(String name);
    Integer getWorkerGroupCount(WorkerGroupQo workerGroupSearchVo);
    List<WorkerGroupVo> getWorkerGroupList(WorkerGroupQo workerGroupSearchVo);
    List<String> getAllWorkerGroupCreator();

}
