package com.vw.isms.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by clg on 2018/3/16.
 */
@Component("userDetailService")
public class UserDetailService implements UserDetailsService {


    private JdbcTemplate jdbcTemplate;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserDetailService(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);//用户仓库，这里不作说明了
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<com.vw.isms.standard.model.User> users = jdbcTemplate.query("select USERNAME, PASSWORD,ROLE, 1 from APP.ISMS_USERS where username=?",new Object[]{username},new RowMapper<com.vw.isms.standard.model.User>(){
            @Override
            public com.vw.isms.standard.model.User mapRow(ResultSet rs, int i) throws SQLException {
                com.vw.isms.standard.model.User u = new com.vw.isms.standard.model.User();
                u.setUserName(rs.getString("USERNAME"));
                u.setPassword(rs.getString("PASSWORD"));
                u.setRole(rs.getString("ROLE"));
                return u;
            }
        });
        com.vw.isms.standard.model.User user = users.size()>0?users.get(0):null;
        if(user==null){
            throw new UsernameNotFoundException("找不到该账户信息！");
        }

        //GrantedAuthority是security提供的权限类，
        //通过ROLE设置用户的类别
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        if(user.getRole()!=null) {
            for (String role : user.getRole().split(",")) {
                //权限如果前缀是ROLE_，security就会认为这是个角色信息，而不是权限，例如ROLE_MENBER就是MENBER角色，CAN_SEND就是CAN_SEND权限
                list.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }
        if(username.equals("useradmin")){
            list.add(new SimpleGrantedAuthority("PER_Admin"));
        }

        //查询用户的menu_url
        List<String> menuUrls = jdbcTemplate.queryForList("select m.MENU_URL from APP.ISMS_MENU m where exists" +
                "(select 1 from APP.ISMS_USER_ROLE ur INNER JOIN APP.ISMS_ROLE_MENU rm on ur.ROLE_ID=rm.ROLE_ID " +
                "where rm.MENU_ID=m.MENU_ID and ur.USERNAME=?)",new Object[]{username},String.class);
        if(menuUrls!=null){
            for(String menuUrl:menuUrls){
                list.add(new SimpleGrantedAuthority("MENU_"+menuUrl));
            }
        }

        User auth_user = new User(user.getUserName(),user.getPassword(),list);//返回包括权限角色的User给security
        return auth_user;
    }
}
