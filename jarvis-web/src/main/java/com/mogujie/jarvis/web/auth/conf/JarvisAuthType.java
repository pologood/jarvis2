package com.mogujie.jarvis.web.auth.conf;

import com.mogu.bigdata.admin.common.passport.conf.AuthType;

/**
 * Created by hejian on 15/9/14.
 */
public enum JarvisAuthType implements AuthType {

    ;
    private Integer code;
    private String name;

    private JarvisAuthType(Integer code, String name){
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
}
