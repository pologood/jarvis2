package com.mogujie.jarvis.web.controller.jarvis;

import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping("/jarvis/trigger")
public class TriggerController extends BaseController{

    @RequestMapping
    @JarvisPassport(authTypes = JarvisAuthType.trigger)
    public String index(){

        return "trigger/index";
    }
}
