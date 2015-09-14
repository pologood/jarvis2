package com.mogujie.jarvis.web.controller.common;

import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.mogu.bigdata.admin.common.entity.User;
import com.mogu.bigdata.admin.common.passport.session.SessionHelper;
import com.mogu.bigdata.admin.common.service.RbacService;
import com.mogu.bigdata.admin.common.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hejian on 15/9/14.
 */
public class BaseController {
    protected ThreadLocal<User> user = new ThreadLocal<User>();
    @Autowired
    protected RbacService rbacService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected CrowdHttpAuthenticator crowdHttpAuthenticator;
    @Autowired
    protected SessionHelper sessionHelper;
    static Logger log = Logger.getLogger(BaseController.class);

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response,
                             @CookieValue(value = SessionHelper.COOKIE_KEY, required = false) String sessionId){
        org.apache.ibatis.logging.LogFactory.useLog4JLogging();
        Object sessionUser = sessionHelper.getSessionUserFromRequest(request);
        if (null != sessionUser){
            user.set((User) sessionUser);
            sessionHelper.updateSession(sessionId, user.get(), response);
        } else {
            user.set(new User());
        }
    }
}
