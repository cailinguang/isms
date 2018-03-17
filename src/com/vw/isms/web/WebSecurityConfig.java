package com.vw.isms.web;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import com.vw.isms.standard.model.Menu;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.web.listener.SuccessLoginHandler;
import com.vw.isms.web.listener.SuccessLogoutHandler;
import freemarker.core.Environment;
import freemarker.template.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private SuccessLogoutHandler logoutHandler;
    @Autowired
    private SuccessLoginHandler loginHandler;
    @Autowired
    private UserDetailsService userDetailService;
    @Autowired
    private LoginAuthenticationProvider loginAuthenticationProvider;

    protected void configure(HttpSecurity http)
            throws Exception {
        http.csrf().disable();


        FormLoginConfigurer<HttpSecurity> urlRegistry =  http.formLogin().defaultSuccessUrl("/standards", false).successHandler(loginHandler)
        .loginPage("/login").usernameParameter("username").passwordParameter("password").failureUrl("/login?error").permitAll();

        urlRegistry.and().logout().addLogoutHandler(logoutHandler).logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll()
        .and().authorizeRequests().antMatchers("/", "/dist/**", "/js/**", "/images/**").permitAll();
        //.and().authorizeRequests().antMatchers("/admin", "/api/admin/**").hasAuthority("PER_Admin")
        //.and().authorizeRequests().antMatchers("/create_standard","/create_evaluation").hasAnyRole(new String[]{"Admin", "User"});


        List<Menu> allMenus = standardRepository.queryAllMenu();
        for(Menu menu:allMenus){
            urlRegistry.and().authorizeRequests().antMatchers(menu.getMenuUrl()).hasAuthority("MENU_"+menu.getMenuUrl());
        }
        urlRegistry.and().authorizeRequests().anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(loginAuthenticationProvider);//.userDetailsService(userDetailService).passwordEncoder(new BCryptPasswordEncoder());
    }


    /**
     * 增加自定义视图变量和方法
     *
     * @return
     */
    @Bean
    public CommandLineRunner customFreemarker(FreeMarkerViewResolver resolver) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                resolver.setViewClass(MyFreemarkerView.class);
                //添加自定义解析器
                Map map = resolver.getAttributesMap();
                map.put("auth", new AuthDirective());
            }
        };
    }


}
class MyFreemarkerView extends FreeMarkerView {

    @Override
    protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
        model.put("contextPath", request.getContextPath());
        model.put("requestURI", request.getRequestURI());
        super.exposeHelpers(model, request);
    }
}

class AuthDirective implements TemplateDirectiveModel {
    Logger logger = Logger.getLogger(AuthDirective.class);


    public void execute(Environment env,Map params, TemplateModel[] loopVars,
                        TemplateDirectiveBody body) throws TemplateException, IOException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }


}
