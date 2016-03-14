package com.mogujie.jarvis.rest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.mogujie.jarvis.rest.sentinel.LogQueryRet;

public class TestTmp {

    public static void main(String[] args) throws IOException {
        String log = Files.readLines(new File("/Users/wuya/Desktop/json.txt"), StandardCharsets.UTF_8).get(0);
        LogQueryRet ret = new Gson().fromJson(log, LogQueryRet.class);
        System.out.println(ret.isSuccess());
    }

}
