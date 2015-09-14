package com.mogujie.jarvis.web.controller.jarvis;

import com.mogu.bigdata.admin.common.entity.Menu;
import com.mogu.bigdata.admin.common.entity.User;
import com.mogu.bigdata.admin.common.exception.BigdataException;
import com.mogu.bigdata.admin.common.passport.conf.PlatformConf;
import com.mogu.bigdata.admin.common.passport.session.SessionHelper;
import com.mogu.bigdata.admin.common.service.RbacService;
import com.mogu.bigdata.admin.common.service.UserPreferService;
import com.mogu.bigdata.admin.common.service.UserService;
import com.mogujie.jarvis.web.auth.adapter.JarvisAuthTypeAdapter;
import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by hejian on 15/9/14.
 */
public class BaseController {
    protected ThreadLocal<User> user = new ThreadLocal<User>();
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    @Autowired
    protected RbacService rbacService;
    @Autowired
    protected UserPreferService userPreferService;
    @Autowired
    protected SessionHelper sessionHelper;
    @Autowired
    protected JarvisAuthTypeAdapter jarvisAuthTypeAdapter;
    @Autowired
    UserService userService;

    static final Logger log = Logger.getLogger(BaseController.class);


    @ModelAttribute
    public void init(HttpServletRequest request,
                     HttpServletResponse response,
                     ModelMap mp,
                     @CookieValue(value = SessionHelper.COOKIE_KEY, required = false) String sessionId) throws BigdataException {
        org.apache.ibatis.logging.LogFactory.useLog4JLogging();
        Object sessionUser = sessionHelper.getSessionUserFromRequest(request);
        if (null != sessionUser){
            this.user.set((User) sessionUser);
            sessionHelper.updateSession(sessionId, user.get(), response);
        } else {
            this.user.set(new User());
        }
        HashMap<Integer, String> urlMap = getUrlMap();
        LinkedHashMap<Long, Menu> menuMap = rbacService.getMenuByPlatformAndUser(urlMap, PlatformConf.report.getCode(), user.get().getUname(),jarvisAuthTypeAdapter.getAll());
        mp.put("platform", PlatformConf.report);
        mp.put("menuMap", menuMap);
        String currentUri = request.getRequestURI();
        Integer permissionId = (Integer) request.getAttribute("permissionId");
        if (null == permissionId) {
            permissionId = 0;
        }
        for(Map.Entry<Long, Menu> menu : menuMap.entrySet()) {
            if (menu.getValue().getPermissionIds().contains(permissionId)) {
                menu.getValue().setIsCurrent(true);
                break;
            }
        }
        mp.put("user", user.get());
        mp.put("currentUri", currentUri);
    }

    private HashMap<Integer, String> getUrlMap(){
        Map<RequestMappingInfo, HandlerMethod> handlerMap = this.handlerMapping.getHandlerMethods();
        HashMap<Integer, String> urlMap = new HashMap<Integer, String>();
        for(Map.Entry m: handlerMap.entrySet()) {
            RequestMappingInfo key = (RequestMappingInfo) m.getKey();
            HandlerMethod value = (HandlerMethod) m.getValue();
            JarvisPassport jarvisPassport = value.getMethodAnnotation(JarvisPassport.class);
            if (null != jarvisPassport) {
                JarvisAuthType[] jarvisAuthTypes = jarvisPassport.authTypes();
                for(JarvisAuthType a: jarvisAuthTypes){
                    TreeSet<String> tmp = new TreeSet<String>(key.getPatternsCondition().getPatterns());
                    urlMap.put(a.getCode(), tmp.first());
                }
            }
        }
        return urlMap;
    }
}
