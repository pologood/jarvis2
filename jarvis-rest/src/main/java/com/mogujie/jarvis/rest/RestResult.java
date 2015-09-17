package com.mogujie.jarvis.rest;

import com.mogujie.jarvis.rest.vo.AbstractVo;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 返回前端的结果
 * @author sanshou
 */
public class RestResult {

	/** 处理是否成功：true成功，false失败 */
	private boolean success;

    /** 错误码 */
    private int code;


    /** 其它错误信息 */

    private String msg;

    /** 异常信息 */
	private String exception;

    /** 数据 */
	private AbstractVo data;

	/**
	 * 构造
	 */
	public RestResult() {}

    /**
     * 构造
     */
    public RestResult(boolean isSuccess) {
        this.success = isSuccess;
    }


	/**
	 * 是否成功
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * 设置是否成功
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

	/**
	 * 获取错误信息
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	* 设置错误信息
	* @param msg the msg to set
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
     * @param exception the exception to set
     */
    public void setException(String exception) {
        this.exception = exception;
    }


    public AbstractVo getData() {
        return data;
    }

    public void setData(AbstractVo data) {
        this.data = data;
    }



}
