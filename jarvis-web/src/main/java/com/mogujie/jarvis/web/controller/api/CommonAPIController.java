package com.mogujie.jarvis.web.controller.api;

import com.mogu.bigdata.admin.core.entity.User;
import com.mogu.bigdata.admin.inside.service.AdminUserService;
import com.mogujie.jarvis.web.utils.MessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.print.attribute.HashAttributeSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejian on 16/1/14.
 */
@Controller
@RequestMapping(value = "/api/common")
public class CommonAPIController {
    @Autowired
    AdminUserService userService;

    /*
    * 获取内网所有用户
    * */
    @RequestMapping(value = "/getAllUser")
    @ResponseBody
    public Object getAllUser() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            List<User> userList = userService.getAllUsers();
            map.put("code", MessageStatus.SUCCESS.getValue());
            map.put("msg","查询用户信息成功");
            map.put("rows",userList);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code",MessageStatus.FAILED.getValue());
            map.put("msg",e.getMessage());
        }
        return map;
    }

    /*
    * 获取返回状态码说明
    * */
    @RequestMapping(value = "/getMessageStatus")
    @ResponseBody
    public Object getMessageStatus(){
        Map<Object, Object> map = new HashMap<Object, Object>();
        MessageStatus[] messageStatuses=MessageStatus.values();
        for(MessageStatus messageStatus:messageStatuses){
            map.put(messageStatus.getValue(),messageStatus.getText());
        }
        return map;
    }

}