package com.mogujie.jarvis.rest;

import com.mogujie.jarvis.rest.vo.JobVo;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by muming on 15/12/1.
 */
public class RestResultTest {


    @Test
    public void test() {

        RestResult result = new RestResult();
        result.setCode(1);
        result.setMsg("msg is welcome!");

        RestResult<JobVo> abc = new RestResult<>();




    }





}
