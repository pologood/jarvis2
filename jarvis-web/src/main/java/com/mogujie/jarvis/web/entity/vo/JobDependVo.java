package com.mogujie.jarvis.web.entity.vo;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.dto.JobDepend;

import java.util.List;

/**
 * Created by hejian on 15/9/22.
 */
public class JobDependVo {
    private Long id;
    private String text;
    private String appName;
    private String appKey;
    private List<JobDependVo> children;
    private JSONObject state;

    public List<JobDependVo> getChildren() {
        return children;
    }

    public void setChildren(List<JobDependVo> children) {
        this.children = children;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public JSONObject getState() {
        return state;
    }

    public void setState(JSONObject state) {
        this.state = state;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
