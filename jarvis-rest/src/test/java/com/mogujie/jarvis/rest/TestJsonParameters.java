package com.mogujie.jarvis.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.rest.utils.JsonParameters;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by muming on 15/12/1.
 */
public class TestJsonParameters {


    @Test
    public void test() {

        String json = "{name:'muming',age:18,sex:false,live:18.9,start:1449504000000}";
        JsonParameters para = new JsonParameters(json);

        String name = para.getString("name");
        Integer age = para.getInteger("age");
        Boolean sex = para.getBoolean("sex");
        Double live = para.getDouble("live");
        Long start = para.getLong("start");

        Assert.assertTrue(name.equals("muming"));
        Assert.assertTrue(age == 18);
        Assert.assertTrue(!sex);
        Assert.assertTrue(live == 18.9);
    }

}
