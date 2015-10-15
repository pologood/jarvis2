package com.mogujie.jarvis.web.controller.jarvis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mogu.bigdata.admin.common.entity.User;
import com.sun.org.apache.xerces.internal.util.URI;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.beans.Encoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejian on 15/10/12.
 */
@Controller
@RequestMapping("/jarvis/remote")
public class RemoteRestApiController extends BaseController {
    String domain="http://10.0.55.120:8080";
    /*
    * 执行远程请求的方法，作为一个client，需要传入参数url与para
    * @param url,远程rest url，必须
    * @param para,json格式的字符串，请求rest url
    * @author hejian
    * */
    @RequestMapping("/request")
    @ResponseBody
    public JSONObject restApi(ModelMap modelMap,String url,String para){
        JSONObject jsonObject=new JSONObject();
        url = domain + url;
        log.info("remote url:"+url+",para:"+para);


        //检查url是否合法
        /*
        boolean url_flag=URI.isWellFormedAddress(url);
        if(!url_flag){
            jsonObject.put("code",1);
            jsonObject.put("msg","url格式不符");
            return jsonObject;
        }
        */

        //检查参数是否合法，如果抛出异常，则不进行下一步解析
        boolean para_flag=true;
        JSONObject paraJson=new JSONObject();
        try {
            paraJson = JSONObject.parseObject(para);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("code",1);
            jsonObject.put("msg", e.getLocalizedMessage());
        }

        if(!para_flag){
            return jsonObject;
        }
        Map<String,String> rawData = new HashMap<String,String>();
        Map<String,String> data = new HashMap<String,String>();
        String newPara="";
        for(Map.Entry entry:paraJson.entrySet()){
            String key=(String)entry.getKey();
            String value=String.valueOf(entry.getValue());
            //key为空字符串的情况过滤掉
            if(key.equals("")){
                continue;
            }
            try {
                rawData.put(key,value);
                //data.put(key,URLEncoder.encode(value,"UTF-8"));
                //newPara=newPara+"&"+key+"="+URLEncoder.encode(value,"UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            User user=(User)modelMap.get("user");
            String uname=user.getUname();
            rawData.put("user",uname);
        } catch (Exception e) {
            e.printStackTrace();
        }



        //请求远程REST服务器。
        try {
            log.info(rawData);
            //log.info(data);
            //log.info(newPara);
            Connection connection=Jsoup.connect(url)
                                    .data(rawData)
                                    .postDataCharset("UTF-8")
                                    .ignoreContentType(true)
                                    .timeout(15000)
                                    .method(Connection.Method.POST);
            Connection.Response response=connection.execute();

            log.info("request url:"+response.url());
            String result=response.body();
            log.info(result);
            JSONObject resultJson = JSON.parseObject(result);
            return resultJson;
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("code",1);
            jsonObject.put("msg",e.getLocalizedMessage());
        }

        return jsonObject;
    }

}
