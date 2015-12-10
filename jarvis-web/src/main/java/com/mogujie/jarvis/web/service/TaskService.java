package com.mogujie.jarvis.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.TaskQo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
import com.mogujie.jarvis.web.mapper.TaskMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hejian on 15/9/17.
 */
@Service
public class TaskService {
    @Autowired
    private TaskMapper taskMapper;

    public TaskVo getTaskById(Long taskId){
        TaskVo taskVo=taskMapper.getTaskById(taskId);
        return taskVo;
    }

    public JSONObject getTasks(TaskQo taskQo){
        JSONObject jsonObject=new JSONObject();

        if(StringUtils.isNotBlank(taskQo.getTaskStatusArrStr())){
            JSONArray arr= JSON.parseArray(taskQo.getTaskStatusArrStr());
            if(arr.size()>0){
                List<Integer> taskStatus= new ArrayList<Integer>();
                for(int i=0;i<arr.size();i++){
                    Integer status=arr.getInteger(i);
                    taskStatus.add(status);
                }
                taskQo.setTaskStatus(taskStatus);
            }
        }

        Integer count = taskMapper.getCountByCondition(taskQo);
        count=count==null?0:count;
        List<TaskVo> taskVoList=taskMapper.getTasksByCondition(taskQo);
        jsonObject.put("total",count);
        jsonObject.put("rows",taskVoList);

        return jsonObject;
    }


    public List<String> getAllExecuteUser(){
        return taskMapper.getAllExecuteUser();
    }
}
