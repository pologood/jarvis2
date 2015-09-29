package com.mogujie.jarvis.web.controller.jarvis;

import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import com.mogujie.jarvis.web.entity.vo.AppVo;
import com.mogujie.jarvis.web.entity.vo.WorkerGroupVo;
import com.mogujie.jarvis.web.entity.vo.WorkerVo;
import com.mogujie.jarvis.web.service.AppService;
import com.mogujie.jarvis.web.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by hejian on 15/9/15.
 */
@Controller
@RequestMapping("/jarvis/manage")
public class ManageController extends BaseController {

    @Autowired
    AppService appService;
    @Autowired
    WorkerService workerService;

    @RequestMapping
    @JarvisPassport(authTypes = JarvisAuthType.manage)
    public String index(){

        return "manage/index";
    }
    @RequestMapping(value = "app")
    @JarvisPassport(authTypes = JarvisAuthType.app)
    public String app(ModelMap modelMap){

        List<String> appNameList=appService.getAllAppName();
        modelMap.put("appNameList",appNameList);
        return "manage/app";
    }
    @RequestMapping(value = "appAddOrEdit")
    public String appAddOrEdit(ModelMap modelMap,Integer appId){
        if(appId!=null){
            AppVo appVo=appService.getAppById(appId);
            modelMap.put("appVo",appVo);
        }
        return "manage/appAddOrEdit";
    }


    @RequestMapping(value = "worker")
    @JarvisPassport(authTypes = JarvisAuthType.worker)
    public String worker(ModelMap modelMap){
        //workerService.getAllWorkerGroupId();
        List<WorkerGroupVo> workerGroupVoList=workerService.getAllWorkerGroup();
        List<String> ipList=workerService.getAllWorkerIp();
        List<Integer> portList=workerService.getAllWorkerPort();

        modelMap.put("workerGroupVoList",workerGroupVoList);
        modelMap.put("ipList",ipList);
        modelMap.put("portList",portList);


        return "manage/worker";
    }

    @RequestMapping(value = "workerAddOrEdit")
    public String workerAddOrEdit(ModelMap modelMap,Integer id){
        if(id!=null){
            WorkerVo workerVo = workerService.getWorkerById(id);
            modelMap.put("workerVo",workerVo);
        }
        List<WorkerGroupVo> workerGroupVoList=workerService.getAllWorkerGroup();
        modelMap.put("workerGroupVoList",workerGroupVoList);
        return "manage/workerAddOrEdit";
    }

    @RequestMapping(value = "workerGroupAddOrEdit")
    public String workerGroupAddOrEdit(ModelMap modelMap,Integer id){
        if(id!=null){
            WorkerGroupVo workerGroupVo=workerService.getWorkerGroupById(id);
            modelMap.put("workerGroupVo",workerGroupVo);
        }
        return "manage/workerGroupAddOrEdit";
    }
}
