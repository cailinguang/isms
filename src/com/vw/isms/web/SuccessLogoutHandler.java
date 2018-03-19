package com.vw.isms.web;

import com.vw.isms.standard.model.AuditLog;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by clg on 2018/3/15.
 */
@Component
public class SuccessLogoutHandler implements LogoutHandler {
    @Autowired
    private StandardRepository repository;

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        AuditLog auditLog = new AuditLog();
        auditLog.setId(IdUtil.next());
        auditLog.setUserName(user.getUsername());
        auditLog.setOperation("登出");
        auditLog.setOperationDate(new Date());
        repository.createAuditLog(auditLog);
    }
}
