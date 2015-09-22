package com.mogujie.jarvis.web.controller.jarvis;

import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping("/jarvis/plan")
public class PlanController extends BaseController{

    @RequestMapping
    @JarvisPassport(authTypes = JarvisAuthType.plan)
    public String index(){

        return "plan/index";
    }

    @RequestMapping(value = "dependency")
    public String detail(ModelMap modelMap,Long jobId){

        return "plan/dependency";
    }
}
