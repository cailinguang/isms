package com.vw.isms.web.filter;

import com.vw.isms.standard.model.Login;
import com.vw.isms.standard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by clg on 2018/3/17.
 */
@Component
@WebFilter(urlPatterns = "/*",filterName = "foceChangePasswordFilter")
public class ChangePasswordFilter implements Filter{
    @Autowired
    private UserRepository userRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    private static long time = 90*24*60*60*1000L;
    private static String reset_url = "/reset_password";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        String url = request.getRequestURI();

        Login login = (Login) request.getSession().getAttribute("login");
        if(login!=null&&login.getLastChangePassTime()!=null){
            if(System.currentTimeMillis()-login.getLastChangePassTime().getTime()>=time){

                if(new AntPathRequestMatcher("/dist/**").matches(request)){
                    filterChain.doFilter(servletRequest,servletResponse);
                    return;
                }
                if(new AntPathRequestMatcher("/js/**").matches(request)){
                    filterChain.doFilter(servletRequest,servletResponse);
                    return;
                }
                if(new AntPathRequestMatcher("/images/**").matches(request)){
                    filterChain.doFilter(servletRequest,servletResponse);
                    return;
                }
                if(new AntPathRequestMatcher(reset_url).matches(request)){
                    filterChain.doFilter(servletRequest,servletResponse);
                    return;
                }

                response.sendRedirect(reset_url+"?foceChange=true");
                return;
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
