package com.mogujie.jarvis.web.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejian on 15/9/17.
 */
public class Constants {
    public static Map<Integer,String> jobFlagMap=new HashMap<Integer,String>();
    public static Map<Integer,String> priorityMap=new HashMap<Integer,String>();

    static {
        jobFlagMap.put(1,"有效");
        jobFlagMap.put(2,"无效");
        jobFlagMap.put(3,"回收站");



    }
}
