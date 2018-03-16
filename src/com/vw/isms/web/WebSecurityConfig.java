package com.vw.isms.web;

import javax.sql.DataSource;

import com.vw.isms.web.listener.SuccessLoginHandler;
import com.vw.isms.web.listener.SuccessLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SuccessLogoutHandler logoutHandler;
    @Autowired
    private SuccessLoginHandler loginHandler;
    @Autowired
    private UserDetailsService userDetailService;

    protected void configure(HttpSecurity http)
            throws Exception {
        http.csrf().disable();

        http.formLogin().defaultSuccessUrl("/standards", false).successHandler(loginHandler)
        .loginPage("/login").usernameParameter("username").passwordParameter("password").failureUrl("/login?error").permitAll()
        .and().logout().addLogoutHandler(logoutHandler).logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll()

        .and().authorizeRequests().antMatchers("/", "/dist/**", "/js/**", "/images/**").permitAll()

        .and().authorizeRequests().antMatchers("/admin", "/api/admin/**").hasAuthority("PER_Admin")
        .and().authorizeRequests().antMatchers("/create_standard","/create_evaluation").hasAnyRole(new String[]{"Admin", "User"})
        .and().authorizeRequests().anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(new BCryptPasswordEncoder());
    }




    //@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        ((JdbcUserDetailsManagerConfigurer) auth.jdbcAuthentication().dataSource(null)
                .passwordEncoder(new BCryptPasswordEncoder()))
                .usersByUsernameQuery("select username, password, 1 from APP.ISMS_USERS where username=?")
                .authoritiesByUsernameQuery("select username, role from APP.ISMS_USERS where username=?");
    }

}
