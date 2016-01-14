package com.mogujie.jarvis.web.controller.api;

import com.mogujie.jarvis.web.entity.qo.BizGroupQo;
import com.mogujie.jarvis.web.entity.vo.BizGroupVo;
import com.mogujie.jarvis.web.service.BizGroupService;
import com.mogujie.jarvis.web.utils.MessageStatus;
import com.mogujie.jarvis.web.utils.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejian on 16/1/13.
 */
@Controller
@RequestMapping(value = "/api/bizGroup")
public class BizGroupApiController {
    @Autowired
    BizGroupService bizGroupService;

    /*
    * 根据id获取bizGroup的相信信息
    * */
    @RequestMapping(value = "getById")
    @ResponseBody
    public Object getById(BizGroupQo bizGroupQo) {
        if (null == bizGroupQo.getId()) {
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("code", MessageStatus.FAILED.getValue());
            map.put("msg","未传入id");
            map.put("supportFields", Tools.getObjectField(BizGroupQo.class));
            return map;
        }
        return bizGroupService.getById(bizGroupQo.getId());
    }

    /*
    * 获取满足条件的所有bizGroup
    * */
    @RequestMapping(value = "getAllByCondition")
    @ResponseBody
    public Object getAll(BizGroupQo bizGroupQo) {
        List<BizGroupVo> list = bizGroupService.getAllByCondition(bizGroupQo);
        return list;
    }

    /*
    * 分页查询
    * */
    @RequestMapping(value = "getPaginationByCondition")
    @ResponseBody
    Object getPaginationByCondition(BizGroupQo bizGroupQo) {
        Map<String, Object> map = bizGroupService.getPaginationByCondition(bizGroupQo);
        return map;
    }
}
