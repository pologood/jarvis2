package com.mogujie.jarvis.web.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.mogujie.jarvis.web.entity.vo.AppQo;
import com.mogujie.jarvis.web.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hejian on 15/9/24.
 */
@Controller
@RequestMapping(value = "/jarvis/api/app")
public class AppAPIController {
    @Autowired
    AppService appService;

    @RequestMapping(value = "getApps")
    @ResponseBody
    public JSONObject getApps(AppQo appSearchVo){
        JSONObject result=appService.getApps(appSearchVo);

        return result;
    }

}
