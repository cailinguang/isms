package com.vw.isms.web;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.AuthorizedUrl;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig
        extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    protected void configure(HttpSecurity http)
            throws Exception {
        http.csrf().disable();

        http.formLogin().defaultSuccessUrl("/standards", false)
        .loginPage("/login").usernameParameter("username").passwordParameter("password").failureUrl("/login?error").permitAll()
        .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll()

        .and().authorizeRequests().antMatchers("/", "/dist/**", "/js/**", "/images/**").permitAll()

        .and().authorizeRequests().antMatchers("/admin", "/api/admin/**").hasAnyRole("ADMIN")
        .and().authorizeRequests().antMatchers("/create_standard","/create_evaluation").hasAnyRole(new String[]{"ADMIN", "USER"})
        .and().authorizeRequests().anyRequest().authenticated();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        ((JdbcUserDetailsManagerConfigurer) auth.jdbcAuthentication().dataSource(this.dataSource)
                .passwordEncoder(new BCryptPasswordEncoder()))
                .usersByUsernameQuery("select username, password, 1 from APP.ISMS_USERS where username=?")
                .authoritiesByUsernameQuery("select username, role from APP.ISMS_USERS where username=?");
    }
}
