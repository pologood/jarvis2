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
        jobFlagMap.put(1,"启用");
        jobFlagMap.put(2,"禁用");
        jobFlagMap.put(3,"过期");
        jobFlagMap.put(4,"回收站");

        priorityMap.put(1,"低");
        priorityMap.put(2,"普通");
        priorityMap.put(3,"高");
        priorityMap.put(4,"很高");

    }
}
