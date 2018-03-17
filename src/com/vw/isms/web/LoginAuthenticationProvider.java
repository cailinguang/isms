package com.vw.isms.web;

import com.vw.isms.standard.model.Login;
import com.vw.isms.standard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by clg on 2018/3/17.
 */
@Component
public class LoginAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public void setBean(UserDetailsService userDetailsService){
        super.setUserDetailsService(userDetailsService);
        super.setPasswordEncoder(new BCryptPasswordEncoder());
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {

            judgeLocked(authentication.getName());

            Authentication auth = super.authenticate(authentication);
            // if reach here, means login success, else exception will be thrown
            // reset the user_attempts
            successLogin(authentication.getName());
            return auth;
        } catch (BadCredentialsException e) {
            failLogin(authentication.getName());
            throw e;
        } catch (LockedException e) {
            throw e;
        }

    }

    public void judgeLocked(String username){
        Date currentDate = new Date();
        Login login = userRepository.queryUserLogin(username);
        if(login!=null){
            if(login.getLoginCount()>=5 && currentDate.getTime()-login.getLastLoginTime().getTime() <= 30*60*1000){
                throw new LockedException("fail login 5 times");
            }
        }
    }

    private void successLogin(String username){
        Login login = userRepository.queryUserLogin(username);
        if(login==null){
            login = new Login();
            login.setUserName(username);
            login.setLoginCount(0);
            login.setLastLoginTime(new Date());
            userRepository.createUserLogin(login);
        }else{
            login.setLoginCount(0);
            login.setLastLoginTime(new Date());
            userRepository.updateUserLogin(login);
        }
    }

    private void failLogin(String username){
        Login login = userRepository.queryUserLogin(username);
        if(login==null){
            login = new Login();
            login.setUserName(username);
            login.setLoginCount(1);
            login.setLastLoginTime(new Date());
            userRepository.createUserLogin(login);
        }else{
            login.setLoginCount(login.getLoginCount()+1);
            login.setLastLoginTime(new Date());
            userRepository.updateUserLogin(login);
        }
    }


}