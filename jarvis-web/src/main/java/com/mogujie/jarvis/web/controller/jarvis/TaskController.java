package com.mogujie.jarvis.web.controller.jarvis;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by hejian on 15/9/14.
 */
@Controller
@RequestMapping("/jarvis/task")
public class TaskController extends BaseController {

    @RequestMapping
    public String index(){

        return "task/index";
    }
}
