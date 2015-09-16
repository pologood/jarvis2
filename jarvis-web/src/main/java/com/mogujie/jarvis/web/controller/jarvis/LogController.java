package com.mogujie.jarvis.web.controller.jarvis;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping("/jarvis/log")
public class LogController extends BaseController{

    @RequestMapping
    public String index(){

        return "log/index";
    }

    @RequestMapping(value = "detail")
    public String detail(ModelMap modelMap,Long jobId){

        return "log/detail";
    }
}
