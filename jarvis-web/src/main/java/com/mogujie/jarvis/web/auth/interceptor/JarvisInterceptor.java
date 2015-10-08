package com.mogujie.jarvis.web.auth.interceptor;

import com.alibaba.fastjson.JSON;
import com.mogu.bigdata.admin.common.entity.User;
import com.mogu.bigdata.admin.common.passport.conf.PlatformConf;
import com.mogu.bigdata.admin.common.passport.conf.ResultType;
import com.mogu.bigdata.admin.common.passport.session.SessionHelper;
import com.mogu.bigdata.admin.common.service.RbacService;
import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hejian on 15/9/14.
 */
public class JarvisInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private RbacService rbacService;
    @Autowired
    private SessionHelper sessionHelper;

    final Logger logger = Logger.getLogger(JarvisInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handler2 = (HandlerMethod) handler;
        JarvisPassport passport = handler2.getMethodAnnotation(JarvisPassport.class);
        if (null == passport) {
            return true;
        }

        User sessionUser = sessionHelper.getSessionUserFromRequest(request);
        if (null == sessionUser || null == sessionUser.getUname() || "".equals(sessionUser.getUname())) {
            if (passport.resultType() == ResultType.page) {
                request.getRequestDispatcher("/").forward(request, response);
            } else if(passport.resultType() == ResultType.json) {
                //ajax页面的登录
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json;charset=UTF-8");
                OutputStream out = response.getOutputStream();
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(out,"utf-8"));
                //返回json格式的提示
                Map<String, Object> result = new HashMap<String, Object>();
                Map<String, Object> status = new HashMap<String, Object>();
                status.put("code", 4005);
                status.put("msg", "没有权限");
                result.put("status", status);
                result.put("message", "没有权限");
                result.put("result", "没有权限");
                String resultStr = JSON.toJSONString(result);
                pw.println(resultStr);
                pw.flush();
                pw.close();
            }
            return false;
        }

        for(JarvisAuthType a: passport.authTypes()) {
            Integer permissionId = a.getCode();
            if (!rbacService.checkPermissionByUser(sessionUser.getUname(), permissionId, PlatformConf.jarvis.getCode())) {
                if (passport.resultType() == ResultType.page) {
                    request.getRequestDispatcher("/jarvis/error?message=没有权限").forward(request, response);
                } else if (passport.resultType() == ResultType.json) {
                    //ajax页面的登录
                    response.setCharacterEncoding("utf-8");
                    response.setContentType("application/json;charset=UTF-8");
                    OutputStream out = response.getOutputStream();
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(out,"utf-8"));
                    //返回json格式的提示
                    Map<String, Object> result = new HashMap<String, Object>();
                    Map<String, Object> status = new HashMap<String, Object>();
                    status.put("code", 4005);
                    status.put("msg", "没有权限");
                    result.put("status", status);
                    result.put("message", "没有权限");
                    result.put("result", "没有权限");
                    String resultStr = JSON.toJSONString(result);
                    pw.println(resultStr);
                    pw.flush();
                    pw.close();
                }
                return false;
            }
        }
        if (0 < passport.authTypes().length) {
            request.setAttribute("permissionId", passport.authTypes()[0].getCode());
        }
        return true;
    }
}
