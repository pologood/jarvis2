package com.mogujie.jarvis.web.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hejian on 15/9/17.
 */
public class TimeTools {
    public static String formatDate(Date date){
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        String result="";

        try {
            result=sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    public static String formatDateTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result="";

        try {
            result=sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
