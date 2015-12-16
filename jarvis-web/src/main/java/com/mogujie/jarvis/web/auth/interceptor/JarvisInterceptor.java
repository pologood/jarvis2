package com.mogujie.jarvis.web.auth.interceptor;

import com.mogu.bigdata.admin.client.service.RbacService;
import com.mogu.bigdata.admin.core.consts.PlatformConfig;
import com.mogu.bigdata.admin.core.entity.User;
import com.mogu.bigdata.admin.passport.conf.ResultType;
import com.mogu.bigdata.admin.passport.session.SessionHelper;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import org.apache.commons.lang3.StringUtils;
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

    static final Logger log = Logger.getLogger(JarvisInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handler2 = (HandlerMethod) handler;
        JarvisPassport passport = handler2.getMethodAnnotation(JarvisPassport.class);
        if (null == passport) {
            return true;
        }

        if (0 < passport.authTypes().length) {
            request.setAttribute("permissionId", passport.authTypes()[0].getCode());
        }

        User sessionUser = sessionHelper.getSessionUserFromRequest(request);
        if (null == sessionUser || StringUtils.isBlank(sessionUser.getUname())) {
            if (passport.resultType() == ResultType.page) {
                request.getRequestDispatcher("/").forward(request, response);
            } else if (passport.resultType() == ResultType.json) {
                //ajax页面的登录
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json;charset=UTF-8");
                OutputStream out = response.getOutputStream();
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "utf-8"));
                //返回json格式的提示
                Map<String, Object> result = new HashMap<String, Object>();
                Map<String, Object> status = new HashMap<String, Object>();
                status.put("code", 4005);
                status.put("msg", "没有权限");
                result.put("status", status);
                result.put("message", "没有权限");
                result.put("result", "没有权限");
                String resultStr = JsonHelper.toJson(result);
                pw.println(resultStr);
                pw.flush();
                pw.close();
            }
            return false;
        }

        if (!passport.needCheck()) {
            return true;
        }

        for (JarvisAuthType a : passport.authTypes()) {
            Integer permissionId = a.getCode();
            if (!rbacService.check(sessionUser.getUname(), permissionId, PlatformConfig.platformId, PlatformConfig.secret)) {
                if (passport.resultType() == ResultType.page) {
                    request.getRequestDispatcher("/error?message=没有权限").forward(request, response);
                } else if (passport.resultType() == ResultType.json) {
                    //ajax页面的登录
                    response.setCharacterEncoding("utf-8");
                    response.setContentType("application/json;charset=UTF-8");
                    OutputStream out = response.getOutputStream();
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "utf-8"));
                    //返回json格式的提示
                    Map<String, Object> result = new HashMap<String, Object>();
                    Map<String, Object> status = new HashMap<String, Object>();
                    status.put("code", 4005);
                    status.put("msg", "没有权限");
                    result.put("status", status);
                    result.put("message", "没有权限");
                    result.put("result", "没有权限");
                    String resultStr = JsonHelper.toJson(result);
                    pw.println(resultStr);
                    pw.flush();
                    pw.close();
                }
                return false;
            }
        }
        return true;
    }
}
