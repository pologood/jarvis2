package com.mogujie.jarvis.web.controller.jarvis;

import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by hejian on 15/12/28.
 */
@Controller
@RequestMapping("/help")
public class HelpController extends BaseController {

    @RequestMapping
    @JarvisPassport(authTypes = JarvisAuthType.help)
    public String index(){
        return "redirect:http://bda.mogujie.org/gitbooks/jarvis2/";
    }
}
