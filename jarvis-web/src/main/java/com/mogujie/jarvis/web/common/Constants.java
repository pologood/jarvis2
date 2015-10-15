package com.mogujie.jarvis.web.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejian on 15/9/17.
 */
public class Constants {
    public final static String appName="jarvis-web";
    public final static String appKey="11111";

    public static Map<Integer,String> jobFlagMap=new HashMap<Integer,String>();
    public static Map<Integer,String> jobPriorityMap=new HashMap<Integer,String>();
    public static Map<Integer,String> taskStatusMap=new HashMap<Integer,String>();
    public static Map<Integer,String> workerStatusMap=new HashMap<Integer,String>();
    public static Map<Integer,String> workerGroupStatusMap=new HashMap<Integer,String>();


    static {
        jobFlagMap.put(1,"启用");
        jobFlagMap.put(2,"禁用");
        jobFlagMap.put(3,"过期");
        jobFlagMap.put(4,"回收站");

        jobPriorityMap.put(1,"低");
        jobPriorityMap.put(2,"普通");
        jobPriorityMap.put(3,"高");
        jobPriorityMap.put(4,"很高");

        taskStatusMap.put(1,"等待");
        taskStatusMap.put(2,"准备好");
        taskStatusMap.put(3,"运行中");
        taskStatusMap.put(4,"成功");
        taskStatusMap.put(5,"失败");
        taskStatusMap.put(6,"强制终止");

        workerStatusMap.put(0,"下线");
        workerStatusMap.put(1,"上线");

        workerGroupStatusMap.put(0,"无效");
        workerGroupStatusMap.put(1,"有效");
    }
}
