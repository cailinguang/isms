package com.vw.isms.web;

import com.vw.isms.standard.repository.StandardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * Created by clg on 2018/3/20.
 */
@Controller
public class ErrorController {
    @Autowired
    private StandardRepository repository;

    private void setupAuth(ModelMap map,HttpSession session) {
        Authentication authentication = ((SecurityContext)session.getAttribute("SPRING_SECURITY_CONTEXT")).getAuthentication();
        map.put("auth", authentication);
        if(authentication!=null){
            map.put("menus",this.repository.queryUserMenu(authentication.getName()));
        }
    }

    @RequestMapping("/400")
    public String badRequest(ModelMap map,HttpSession session){
        setupAuth(map,session);
        return "error/400";
    }

    @RequestMapping("/403")
    public String forbidden(ModelMap map, HttpSession session){
        setupAuth(map,session);
        return "error/403";
    }

    @RequestMapping("/404")
    public String notFound(ModelMap map, HttpSession session){
        setupAuth(map,session);
        return "error/404";
    }

}
