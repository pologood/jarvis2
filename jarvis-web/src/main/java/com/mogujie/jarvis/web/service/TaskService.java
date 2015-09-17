package com.mogujie.jarvis.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.TaskSearchVo;
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
        return taskMapper.getTaskById(taskId);
    }

    public JSONObject getTasks(TaskSearchVo taskSearchVo){
        JSONObject jsonObject=new JSONObject();
        Integer count = taskMapper.getCountByCondition(taskSearchVo);
        count=count==null?0:count;


        if(StringUtils.isNotBlank(taskSearchVo.getTaskStatusArrStr())){
            JSONArray arr= JSON.parseArray(taskSearchVo.getTaskStatusArrStr());
            if(arr.size()>0){
                List<Integer> taskStatus= new ArrayList<Integer>();
                for(int i=0;i<arr.size();i++){
                    Integer status=arr.getInteger(i);
                    taskStatus.add(status);
                }
                taskSearchVo.setTaskStatus(taskStatus);
            }
        }


        List<TaskVo> taskVoList=taskMapper.getTasksByCondition(taskSearchVo);


        jsonObject.put("total",count);
        jsonObject.put("rows",taskVoList);

        return jsonObject;
    }
}
