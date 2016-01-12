package com.mogujie.jarvis.web.entity.vo;

import com.mogujie.jarvis.dto.generate.JobDepend;

import java.util.List;
import java.util.Map;

/**
 * Created by hejian on 15/9/22.
 */
public class JobDependVo {
    private Long id;
    private String text;
    private String name;
    private Long value;
    private String appName;
    private String appKey;
    private List<JobDependVo> children;
    private List<JobDependVo> parents;
    private boolean parentFlag=false;
    private boolean rootFlag=false;
    private Map<String,Object> state;
    private Integer commonStrategy;
    private String offsetStrategy;


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

    public Map<String, Object> getState() {
        return state;
    }

    public void setState(Map<String, Object> state) {
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

    public List<JobDependVo> getParents() {
        return parents;
    }

    public void setParents(List<JobDependVo> parents) {
        this.parents = parents;
    }

    public boolean isParentFlag() {
        return parentFlag;
    }

    public void setParentFlag(boolean parentFlag) {
        this.parentFlag = parentFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public boolean isRootFlag() {
        return rootFlag;
    }

    public void setRootFlag(boolean rootFlag) {
        this.rootFlag = rootFlag;
    }

    public Integer getCommonStrategy() {
        return commonStrategy;
    }

    public void setCommonStrategy(Integer commonStrategy) {
        this.commonStrategy = commonStrategy;
    }

    public String getOffsetStrategy() {
        return offsetStrategy;
    }

    public void setOffsetStrategy(String offsetStrategy) {
        this.offsetStrategy = offsetStrategy;
    }
}
