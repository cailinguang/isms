package com.vw.isms.web.listener;

import com.vw.isms.standard.model.AuditLog;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by clg on 2018/3/15.
 */
@Component
public class SuccessLoginHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private StandardRepository repository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        super.onAuthenticationSuccess(request, response, authentication);

        User user = (User) authentication.getPrincipal();

        AuditLog auditLog = new AuditLog();
        auditLog.setId(IdUtil.next());
        auditLog.setUserName(user.getUsername());
        auditLog.setOperation("登录");
        auditLog.setOperationDate(new Date());
        repository.createAuditLog(auditLog);
    }
}
