package com.mogujie.jarvis.web.auth.conf;

import com.mogu.bigdata.admin.passport.conf.AuthType;

/**
 * Created by hejian on 15/9/14.
 */
public enum JarvisAuthType implements AuthType {

    plan(1001,"执行计划"),
    task(2001,"执行流水"),
    job(3001,"任务管理"),
    trigger(4001,"重跑任务"),
    manage_system(5001,"调度系统管理"),
    manage_app(5002,"应用管理"),
    manage_worker(5003,"worker管理")
    ;
    private Integer code;
    private String name;

    JarvisAuthType(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getCode() {
        return code;
    }
    public String getName() {
        return name;
    }

    public static String getNameByCode(Integer code) {
        for(JarvisAuthType defaultAuthType: JarvisAuthType.values()) {
            if (code.equals(defaultAuthType.getCode())) {
                return defaultAuthType.getName();
            }
        }
        return null;
    }

}
