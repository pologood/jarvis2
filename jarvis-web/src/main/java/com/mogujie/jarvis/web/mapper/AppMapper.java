package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.AppSearchVo;
import com.mogujie.jarvis.web.entity.vo.AppVo;

import java.util.List;

/**
 * Created by hejian on 15/9/24.
 */
public interface AppMapper {
    public AppVo getAppById(Integer appId);
    public List<String> getAllAppName();
    public Integer getAppCount(AppSearchVo appSearchVo);
    public List<AppVo> getAppList(AppSearchVo appSearchVo);
    public AppVo getAppByName(String appName);
}
