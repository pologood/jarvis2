package com.mogujie.jarvis.rest;

/**
 * REST结果
 * 
 * @author 牧名
 */
public class RestResult<T> {

    /** 错误码 */
    private int code;
    /** 错误信息 */
    private String msg;
    /** 数据 */
    private T data;
    /**
     * 构造
     */
    public RestResult() {
    }
    /**
     * 构造
     */
    public RestResult(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

}
