package com.mogujie.jarvis.rest.utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hejian on 15/10/19.
 */
public class RequestUtils {

    /*
    * 验证必须参数
    *
    * */
    public static JSONObject checkPara(JSONObject para,String... fields){
        JSONObject jsonObject= new JSONObject();
        boolean flag=true;
        List<String> data=new ArrayList<String>();
        for(String field:fields){
            Object value=para.get(field);
            if(value==null){
                flag=false;
                data.add(field);
            }
        }

        //全部有参数
        if(flag==true){
            jsonObject.put("code",0);
        }
        else{
            jsonObject.put("code",1);
            String fieldStr="";
            for(String field:data){
                fieldStr=fieldStr+field;
            }
            jsonObject.put("msg",fieldStr+"不能为空");
        }

        return jsonObject;
    }
}
