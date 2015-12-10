package com.mogujie.jarvis.web.service;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.AppQo;
import com.mogujie.jarvis.web.entity.vo.AppVo;
import com.mogujie.jarvis.web.mapper.AppMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hejian on 15/9/24.
 */
@Service
public class AppService {
    @Autowired
    AppMapper appMapper;

    //获取应用列表
    public JSONObject getApps(AppQo appSearchVo){
        JSONObject jsonObject =new JSONObject();

        Integer count = appMapper.getAppCount(appSearchVo);
        List<AppVo> appVoList=appMapper.getAppList(appSearchVo);

        jsonObject.put("total",count);
        jsonObject.put("rows",appVoList);

        return jsonObject;
    }
    public List<AppVo> getAppList(AppQo appSearchVo){
        List<AppVo> appVoList=appMapper.getAppList(appSearchVo);
        return appVoList;
    }

    public List<String> getAllAppName(){
        return appMapper.getAllAppName();
    }
    public AppVo getAppById(Integer appId){
        return appMapper.getAppById(appId);
    }
    public AppVo getAppByName(String appName){
        return appMapper.getAppByName(appName);
    }

}
