package com.mogujie.jarvis.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.JobDependVo;
import com.mogujie.jarvis.web.mapper.JobDependMapper;
import com.mogujie.jarvis.web.entity.vo.JobSearchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hejian on 15/9/22.
 */
@Service
public class JobDependService {
    @Autowired
    JobDependMapper jobDependMapper;

    public JSONObject getTreeDependedONJob(JobSearchVo jobSearchVo){
        JSONObject jsonObject=new JSONObject();
        JobDependVo jobDependVo=jobDependMapper.getJobById(jobSearchVo.getJobId());

        if(jobDependVo==null){
            return jsonObject;
        }
        JSONObject jsonState =new JSONObject();
        jsonState.put("opened",true);

        List<JobDependVo> jobDependVoList=getChildren(jobDependVo);
        jobDependVo.setChildren(jobDependVoList);
        jsonObject=(JSONObject)JSON.toJSON(jobDependVo);

        jsonObject.put("state",jsonState);

        return jsonObject;
    }

    public List<JobDependVo> getChildren(JobDependVo jobDependVo){
        List<JobDependVo> jobChildren=jobDependMapper.getChildrenById(jobDependVo.getId());
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("opened",true);
        if(jobChildren!=null&&jobChildren.size()>0){
            for(JobDependVo childJob:jobChildren){
                childJob.setState(jsonObject);
                childJob.setChildren(getChildren(childJob));
            }
        }

        return jobChildren;
    }
}
