package com.mogujie.jarvis.web.controller.jarvis;

import com.atlassian.crowd.model.user.User;
import com.mogu.bigdata.admin.common.passport.conf.PlatformConf;
import com.mogu.bigdata.admin.common.passport.session.SessionHelper;
import com.mogu.bigdata.admin.common.util.JsonReturn;
import com.mogujie.jarvis.web.controller.jarvis.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hejian on 15/9/14.
 */
@Controller
public class IndexController extends BaseController{

    @RequestMapping("/")
    public String index(ModelMap model,HttpServletRequest request, HttpServletResponse response){
        if (null == user || null == user.get().getUname() || StringUtils.isBlank(user.get().getUname())) {
            model.addAttribute("platform", PlatformConf.jarvis);
            return "index";
        } else {
            model.clear();
            model.put("user",user.get());
            return "redirect:plan";
        }
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public JsonReturn login(HttpServletRequest request, HttpServletResponse response, String uname, String password){
        try {
            crowdHttpAuthenticator.authenticate(request, response, uname, password);
            if (crowdHttpAuthenticator.isAuthenticated(request, response)) {
                User crowdUser = crowdHttpAuthenticator.getUser(request);
                user.get().setUname(uname);
                user.get().setNick(crowdUser.getDisplayName());
                user.get().setEmail(crowdUser.getEmailAddress());
                userService.insertUser(user.get());
                user.set(userService.getUser(uname));
                String sessionId = SessionHelper.genSessionId();
                sessionHelper.updateSession(sessionId, user.get(), response);
                return new JsonReturn(1001, "/jarvis");
            } else {
                log.error("login fail");
                return new JsonReturn(4004);
            }
        } catch (Exception e){
            log.error(e.getLocalizedMessage(), e);
            return new JsonReturn(4001, e.getMessage());
        }
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletResponse response, @CookieValue(value = SessionHelper.COOKIE_KEY, required = false) String sessionId){
        sessionHelper.clearSession(sessionId, response);
        return "redirect:/";
    }
}
