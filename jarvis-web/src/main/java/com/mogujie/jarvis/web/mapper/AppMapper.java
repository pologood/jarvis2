package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.AppQo;
import com.mogujie.jarvis.web.entity.vo.AppVo;

import java.util.List;

/**
 * Created by hejian on 15/9/24.
 */
public interface AppMapper {
    AppVo getAppById(Integer appId);
    List<String> getAllAppName();
    Integer getAppCount(AppQo appSearchVo);
    List<AppVo> getAppList(AppQo appSearchVo);
    AppVo getAppByName(String appName);
}
