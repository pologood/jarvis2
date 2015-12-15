package com.mogujie.jarvis.web.controller.jarvis;

import com.alibaba.fastjson.JSONObject;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.mogu.bigdata.admin.client.service.MenuService;
import com.mogu.bigdata.admin.client.service.RbacService;
import com.mogu.bigdata.admin.core.consts.PlatformConfig;
import com.mogu.bigdata.admin.core.entity.User;
import com.mogu.bigdata.admin.core.entity.vo.Menu;
import com.mogu.bigdata.admin.core.exception.BigdataException;
import com.mogu.bigdata.admin.inside.service.AdminUserService;
import com.mogu.bigdata.admin.passport.session.SessionHelper;
import com.mogujie.jarvis.web.auth.adapter.JarvisAuthTypeAdapter;
import com.mogujie.jarvis.web.auth.annotation.JarvisPassport;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;
import org.apache.commons.lang.StringUtils;
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
import java.util.*;

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
    protected SessionHelper sessionHelper;
    @Autowired
    protected AdminUserService userService;
    @Autowired
    protected CrowdHttpAuthenticator crowdHttpAuthenticator;
    @Autowired
    private MenuService menuService;

    static final Logger log = Logger.getLogger(BaseController.class);


    @ModelAttribute
    public void init(HttpServletRequest request, ModelMap mp, HttpServletResponse response,
                     @CookieValue(value = SessionHelper.COOKIE_KEY, required = false) String sessionId) throws BigdataException {
        org.apache.ibatis.logging.LogFactory.useLog4JLogging();
        mp.put("contextPath", request.getContextPath());
        // user
        initUser(mp, request, response, sessionId);
        initPlatform(mp);
        if (null == user.get() || StringUtils.isBlank(user.get().getUname())) {
            return;
        }
        initMenu(request, mp, handlerMapping);
        mp.put("user", user.get());
    }


    private void initUser(ModelMap mp, HttpServletRequest request, HttpServletResponse response, String sessionId) {
        Object sessionUser = sessionHelper.getSessionUserFromRequest(request);
        if (null != sessionUser){
            this.user.set((User) sessionUser);
            sessionHelper.updateSession(sessionId, user.get(),response);
        } else {
            this.user.set(new User());
        }
        mp.put("user", user.get());
    }


    private void initPlatform(ModelMap mp) {
        mp.put("platformName", PlatformConfig.name);
    }


    private void initMenu(HttpServletRequest request, ModelMap mp, RequestMappingHandlerMapping handlerMapping) {
        List<Menu> menus = menuService.get(user.get().getUname(), PlatformConfig.platformId, PlatformConfig.secret);
        if (null == menus) {
            return;
        }
        Integer permissionId = (Integer) request.getAttribute("permissionId");
        String uri=request.getRequestURI();
        uri=uri.substring(uri.indexOf("/")+1);
        uri=uri.substring(uri.indexOf("/"));
        mp.put("currentUri",uri);

        if (null == permissionId) permissionId = 0;
        boolean hasCurrent = false;
        Map<Integer, Map<String, String>> urlMap = getUrlMap(handlerMapping);
        for(Menu menu: menus) {
            if (!hasCurrent && menu.getPermissionIds().contains(permissionId)) {
                menu.setIsCurrent(true);
                hasCurrent = true;
            }
            for(Integer p: menu.getPermissionIds()) {
                if (urlMap.containsKey(p)) {
                    menu.putUrl(urlMap.get(p));
                }
            }
        }
        mp.put("menu", menus);
    }


    private Map<Integer, Map<String, String>> getUrlMap(RequestMappingHandlerMapping handlerMapping) {
        Map<RequestMappingInfo, HandlerMethod> handlerMap = handlerMapping.getHandlerMethods();
        Map<Integer, Map<String, String>> re = new HashMap<Integer, Map<String, String>>();
        for(Map.Entry<RequestMappingInfo, HandlerMethod> m: handlerMap.entrySet()) {
            RequestMappingInfo key = m.getKey();
            HandlerMethod value = m.getValue();
            JarvisPassport passport = value.getMethodAnnotation(JarvisPassport.class);
            if (null != passport && passport.isMenu()) {
                JarvisAuthType[] authTypes = passport.authTypes();
                for(JarvisAuthType a: authTypes){
                    TreeSet<String> tmp = new TreeSet<String>(key.getPatternsCondition().getPatterns());
                    Map<String, String> v = new HashMap<String, String>();
                    v.put(JarvisAuthType.getNameByCode(a.getCode()), tmp.first());
                    re.put(a.getCode(), v);
                }
            }
        }
        return re;
    }
}
