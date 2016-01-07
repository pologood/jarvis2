package com.mogujie.jarvis.web.service;

import com.mogujie.jarvis.web.entity.qo.WorkerGroupQo;
import com.mogujie.jarvis.web.entity.vo.WorkerGroupVo;
import com.mogujie.jarvis.web.entity.qo.WorkerQo;
import com.mogujie.jarvis.web.entity.vo.WorkerVo;
import com.mogujie.jarvis.web.mapper.WorkerMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejian on 15/9/28.
 */

@Service
public class WorkerService {
    @Autowired
    WorkerMapper workerMapper;

    /*
    * 根据id获取worker
    * */
    public WorkerVo getWorkerById(Integer id) {
        return workerMapper.getWorkerById(id);
    }

    public WorkerVo getWorkerByIpAndPort(Map<String, Object> para) {
        return workerMapper.getWorkerByIpAndPort(para);
    }

    /*
    * 根据条件获取worker数
    * */
    public Integer getWorkerCount(WorkerQo workerSearchVo) {
        return workerMapper.getWorkerCount(workerSearchVo);
    }

    /*
    * 根据条件查询worker列表
    * */
    public Map<String, Object> getWorkers(WorkerQo workerQo) {
        Integer total = workerMapper.getWorkerCount(workerQo);
        List<WorkerVo> workerVoList = workerMapper.getWorkerList(workerQo);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("total", total);
        result.put("rows", workerVoList);
        return result;
    }

    /*
    * 获取IP信息列表
    * */
    public List<String> getAllWorkerIp() {
        return workerMapper.getAllWorkerIp();
    }

    /*
    * 获取端口信息列表
    * */
    public List<Integer> getAllWorkerPort() {
        return workerMapper.getAllWorkerPort();
    }


    /*
    * 获取
    * */
    public List<WorkerGroupVo> getAllWorkerGroup() {
        return workerMapper.getAllWorkerGroup();
    }

    /*
    * 根据id获取workerGroup信息
    * */
    public WorkerGroupVo getWorkerGroupById(Integer id) {
        return workerMapper.getWorkerGroupById(id);
    }

    public WorkerGroupVo getWorkerGroupByName(String name) {
        return workerMapper.getWorkerGroupByName(name);
    }

    /*
    * 根据条件获取workerGroup数量
    * */
    public Integer getWorkerGroupCount(WorkerGroupQo workerGroupSearchVo) {
        return workerMapper.getWorkerGroupCount(workerGroupSearchVo);
    }

    /*
    * 根据条件获取WorkerGroup列表
    * */
    public Map<String, Object> getWorkerGroups(WorkerGroupQo workerGroupSearchVo) {
        Integer total = workerMapper.getWorkerGroupCount(workerGroupSearchVo);
        List<WorkerGroupVo> workerGroupVoList = workerMapper.getWorkerGroupList(workerGroupSearchVo);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("total", total);
        result.put("rows", workerGroupVoList);
        return result;
    }

    /*
    * 获取所有WorkerGroup的创建者
    * */
    public List<String> getAllWorkerGroupCreator() {
        return workerMapper.getAllWorkerGroupCreator();
    }

}
