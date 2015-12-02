package com.mogujie.jarvis.rest;

import com.mogujie.jarvis.rest.vo.AbstractVo;

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

    /** 异常信息 */
    private String exception;

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


    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取错误信息
     * 
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置错误信息
     * 
     * @param msg
     *            the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the exception
     */
    public String getException() {
        return exception;
    }

    /**
     * @param exception
     *            the exception to set
     */
    public void setException(String exception) {
        this.exception = exception;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
