package com.mogujie.jarvis.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.PlanSearchVo;
import com.mogujie.jarvis.web.entity.vo.PlanVo;
import com.mogujie.jarvis.web.entity.vo.TaskVo;
import com.mogujie.jarvis.web.mapper.PlanMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hejian on 15/10/21.
 */
@Service
public class PlanService {
    @Autowired
    PlanMapper planMapper;

    public JSONObject getPlans(PlanSearchVo planSearchVo){
        JSONObject jsonObject=new JSONObject();
        /*
        if(StringUtils.isNotBlank(planSearchVo.getTaskStatusArrStr())){
            JSONArray arr= JSON.parseArray(planSearchVo.getTaskStatusArrStr());
            if(arr.size()>0){
                List<Integer> taskStatus= new ArrayList<Integer>();
                for(int i=0;i<arr.size();i++){
                    Integer status=arr.getInteger(i);
                    taskStatus.add(status);
                }
                planSearchVo.setTaskStatus(taskStatus);
            }
        }
        */

        Integer count = planMapper.getCountByCondition(planSearchVo);
        count=count==null?0:count;
        List<PlanVo> taskVoList=planMapper.getPlansByCondition(planSearchVo);
        jsonObject.put("total",count);
        jsonObject.put("rows",taskVoList);

        return jsonObject;
    }
}
