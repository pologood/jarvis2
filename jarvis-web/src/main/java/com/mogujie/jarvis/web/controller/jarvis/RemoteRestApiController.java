package com.mogujie.jarvis.web.controller.jarvis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.hash.Hashing;
import com.mogu.bigdata.admin.core.entity.User;
import com.mogujie.jarvis.web.common.Constants;
import com.sun.org.apache.xerces.internal.util.URI;
import org.apache.http.client.utils.URLEncodedUtils;
import org.joda.time.DateTime;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.beans.Encoder;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by hejian on 15/10/12.
 */
@Controller
@RequestMapping("/remote")
public class RemoteRestApiController extends BaseController {
    static String domain="";


    static {
        try {
            InputStream inputStream=RemoteRestApiController.class.getClassLoader().getResourceAsStream("api.properties");
            Properties properties=new Properties();
            properties.load(inputStream);
            domain=properties.getProperty("rest.domain");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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

        Map<String,String> data = new HashMap<String,String>();
        data.put("parameters",para);
        try {
            User user=(User)modelMap.get("user");
            String uname=user.getUname();
            data.put("user",uname);

            DateTime dateTime= new DateTime();
            Long timeStamp=dateTime.getMillis()/1000;
            String token= generateToken(timeStamp, Constants.appKey);

            data.put("appToken", token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data.put("appName", Constants.appName);
        data.put("appKey", Constants.appKey);

        //请求远程REST服务器。
        try {
            log.info(data);
            //log.info(data);
            //log.info(newPara);
            Connection connection=Jsoup.connect(url)
                                    .data(data)
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

    public static String generateToken(long timestamp, String appKey) {
        return timestamp + Hashing.md5().hashString(appKey + timestamp, StandardCharsets.UTF_8).toString();
    }

}
