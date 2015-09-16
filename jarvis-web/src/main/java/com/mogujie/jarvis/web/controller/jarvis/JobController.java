package com.mogujie.jarvis.web.controller.jarvis;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping("/jarvis/job")
public class JobController extends BaseController{

    @RequestMapping
    public String index(){

        return "job/index";
    }

    @RequestMapping(value = "addOrEdit")
    public String addOrEdit(ModelMap modelMap,@RequestParam(defaultValue = "add") String operation){

        return "job/addOrEdit";
    }

    @RequestMapping(value = "dependency")
    public String dependency(ModelMap modelMap){

        return "job/dependency";
    }

    @RequestMapping(value = "trash")
    public String trash(ModelMap modelMap){

        return "job/trash";
    }
}
