package com.vw.isms.standard.repository;

import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.Login;
import com.vw.isms.standard.model.Role;
import com.vw.isms.standard.model.User;
import com.vw.isms.util.PasswordUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

public class UserRepository
{
  private final NamedParameterJdbcTemplate namedTemplate;
  private final JdbcTemplate jdbcTemplate;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private static final String SCHEMA = "APP";
  
  public UserRepository(JdbcTemplate template)
  {
    this.namedTemplate = new NamedParameterJdbcTemplate(template);
    this.jdbcTemplate = template;
  }

  //USER role字段用来当做readonly属性
  public String createUser(User user)
    throws RepositoryException
  {
    try
    {
      String rawPassword = PasswordUtil.randomCompliantPassword(user.getUserName());
      String password = this.passwordEncoder.encode(rawPassword);
      SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
      insertion
        .withSchema("APP")
        .withTable("ISMS_USERS")
        .withColumnValue("USERNAME", user.getUserName())
        .withColumnValue("PASSWORD", password)
        .withColumnValue("ROLE", user.getRole())
        .withColumnValue("TEL", user.getTel())
        .withColumnValue("REALNAME", user.getRealName())
        .withColumnValue("EMAIL", user.getEmail())
        .withColumnValue("DEPARTMENT", user.getDepartment());
      insertion.insert(this.jdbcTemplate);
      return rawPassword;
    }
    catch (Throwable t)
    {
      throw new RepositoryException(t.getMessage());
    }
  }
  
  public PagingResult<User> queryUser(UserSearchRequest search)
    throws RepositoryException
  {
    try
    {
      Map<String, Object> values = new HashMap();
      StringBuilder builder = new StringBuilder();
      builder.append("SELECT u.*,d.DEPT_NAME FROM APP.ISMS_USERS u left join APP.ISMS_DEPT d on u.DEPARTMENT=d.DEPT_ID");
      if (!StringUtils.isEmpty(search.getNamePattern()))
      {
        values.put("namePattern", "%" + search.getNamePattern() + "%");
        builder.append(" WHERE USERNAME like :namePattern");
      }
      PagingResultSetExtractor<User> handler = new PagingResultSetExtractor(search.getPageNumber(), search.getItemPerPage())
      {
        public User mapRow(ResultSet rs)
          throws SQLException
        {
          User user = new User();
          user.setUserName(rs.getString("USERNAME"));
          user.setRole(rs.getString("ROLE"));
          user.setEmail(rs.getString("EMAIL"));
          user.setRealName(rs.getString("REALNAME"));
          user.setTel(rs.getString("TEL"));
          user.setDepartment(rs.getString("DEPARTMENT"));
          user.setDepartmentName(rs.getString("DEPT_NAME"));
          return user;
        }

          @Override
          public int count() {
              return namedTemplate.queryForObject(builder.toString().replace("u.*,d.DEPT_NAME","count(*)"),values,Integer.class);
          }
      };
      return (PagingResult)this.namedTemplate.query(builder.toString(), values, handler);
    }
    catch (Throwable t)
    {
      t.printStackTrace();
      throw new RepositoryException(t.getMessage());
    }
  }
  
  public User getUser(String username)
    throws RepositoryException
  {
      List<User> users = this.jdbcTemplate.query("select u.*,d.DEPT_NAME from APP.ISMS_USERS u LEFT JOIN APP.ISMS_DEPT d on u.DEPARTMENT=d.DEPT_ID where USERNAME=?", new Object[]{username}, new RowMapper<User>() {
          @Override
          public User mapRow(ResultSet rs, int i) throws SQLException {
              User user = new User();
              user.setUserName(rs.getString("USERNAME"));
              user.setPassword(rs.getString("PASSWORD"));
              user.setRole(rs.getString("ROLE"));
              user.setEmail(rs.getString("EMAIL"));
              user.setRealName(rs.getString("REALNAME"));
              user.setTel(rs.getString("TEL"));
              user.setDepartment(rs.getString("DEPARTMENT"));
              user.setDepartmentName(rs.getString("DEPT_NAME"));
              return user;
          }
      });

      return users.size()>0?users.get(0):null;

  }
  
  public void deleteUser(User user)
    throws RepositoryException
  {
    try
    {
      SimpleJdbcDelete deleteStandard = new SimpleJdbcDelete();
      deleteStandard
        .withSchema("APP")
        .withTable("ISMS_USERS")
        .withKey("USERNAME", user.getUserName());
      deleteStandard.delete(this.namedTemplate);
    }
    catch (Throwable t)
    {
      t.printStackTrace();
      throw new RepositoryException(t.getMessage());
    }
  }
  
  public boolean updatePassword(String username, String oldPassword, String newPassword)
    throws RepositoryException
  {
    User user = getUser(username);
    if (!this.passwordEncoder.matches(oldPassword, user.getPassword())) {
      return false;
    }
    if (PasswordUtil.isCompliantPassword("admin".equals(username)?PasswordUtil.adminUserLength:PasswordUtil.normalUserLength,user.getUserName(),newPassword))
    {
      changePassword(user, newPassword);
      return true;
    }
    return false;
  }
  
  public void changePassword(User user, String newPassword)
    throws RepositoryException
  {
    try
    {
      SimpleJdbcUpdate update = new SimpleJdbcUpdate();
      update
        .withSchema("APP")
        .withTable("ISMS_USERS")
        .withKey("USERNAME", user.getUserName())
        .withColumnValue("PASSWORD", this.passwordEncoder.encode(newPassword));
      update.update(this.namedTemplate);
    }
    catch (Throwable t)
    {
      t.printStackTrace();
      throw new RepositoryException(t.getMessage());
    }
  }
  
  public String resetPassword(String username)
    throws RepositoryException
  {
    try
    {
      String rawPassword = PasswordUtil.randomCompliantPassword(username);
      String password = this.passwordEncoder.encode(rawPassword);
      SimpleJdbcUpdate update = new SimpleJdbcUpdate();
      update
        .withSchema("APP")
        .withTable("ISMS_USERS")
        .withKey("USERNAME", username)
        .withColumnValue("PASSWORD", password);
      update.update(this.namedTemplate);
      return rawPassword;
    }
    catch (Throwable t)
    {
      t.printStackTrace();
      throw new RepositoryException(t.getMessage());
    }
  }
  
  public void updateUser(User user)
    throws RepositoryException
  {
    try
    {
      SimpleJdbcUpdate update = new SimpleJdbcUpdate();
      update
        .withSchema("APP")
        .withTable("ISMS_USERS")
        .withKey("USERNAME", user.getUserName())
        .withColumnValue("REALNAME", user.getRealName())
        .withColumnValue("DEPARTMENT", user.getDepartment())
        .withColumnValue("EMAIL", user.getEmail())
        .withColumnValue("TEL", user.getTel())
        .withColumnValue("ROLE",user.getRole());
      update.update(this.namedTemplate);
    }
    catch (Throwable t)
    {
      t.printStackTrace();
      throw new RepositoryException(t.getMessage());
    }
  }


  public List<Role> getUserRoles(String username){
      return this.jdbcTemplate.query("select * from APP.ISMS_ROLE r where EXISTS (select 1 from APP.ISMS_USER_ROLE ur where r.ROLE_ID=ur.ROLE_ID and ur.USERNAME=?)", new Object[]{username}, new RowMapper<Role>(){
          @Override
          public Role mapRow(ResultSet rs, int i) throws SQLException {
              Role role = new Role();
              role.setRoleId(rs.getString("ROLE_ID"));
              role.setRoleName(rs.getString("ROLE_NAME"));
              return role;
          }
      });
  }

  public void assignUserRole(String username,String... roles){
      this.jdbcTemplate.update("delete from APP.ISMS_USER_ROLE where USERNAME=?",username);
      if(roles!=null)
      this.jdbcTemplate.batchUpdate("INSERT INTO APP.ISMS_USER_ROLE(USERNAME,ROLE_ID) VALUES (?,?)", new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
              preparedStatement.setString(1,username);
              preparedStatement.setString(2,roles[i]);
          }

          @Override
          public int getBatchSize() {
              return roles.length;
          }
      });
  }

  public Login queryUserLogin(String username){
      List<Login> logins = this.jdbcTemplate.query("select * from APP.ISMS_LOGIN where USERNAME=?", new Object[]{username}, new RowMapper<Login>() {
          @Override
          public Login mapRow(ResultSet rs, int i) throws SQLException {
              Login login = new Login();
              login.setUserName(rs.getString("USERNAME"));
              login.setLastLoginTime(rs.getTimestamp("LAST_LOGTIN_TIME"));
              login.setLoginCount(rs.getInt("LOGIN_COUNT"));
              login.setLastSixPassword(rs.getString("LAST_SIX_PASSWORD"));
              login.setLastChangePassTime(rs.getTimestamp("LAST_CHANGE_PASS_TIME"));
              return login;
          }
      });
      return logins.size()>0?logins.get(0):null;
  }

  public void updateUserLogin(Login login){
      SimpleJdbcUpdate update = new SimpleJdbcUpdate();
      update.withSchema("APP").withTable("ISMS_LOGIN").withKey("USERNAME",login.getUserName())
              .withColumnValue("LAST_LOGTIN_TIME",login.getLastLoginTime())
              .withColumnValue("LOGIN_COUNT",login.getLoginCount())
              .withColumnValue("LAST_CHANGE_PASS_TIME",login.getLastChangePassTime())
              .withColumnValue("LAST_SIX_PASSWORD",login.getLastSixPassword());
      update.update(this.namedTemplate);
  }

  public void createUserLogin(Login login){
      if(this.jdbcTemplate.queryForObject("select count(*) from APP.ISMS_USERS where USERNAME=?",new Object[]{login.getUserName()},Integer.class)>0){
          SimpleJdbcInsertion insertion = new SimpleJdbcInsertion();
          insertion.withSchema("APP").withTable("ISMS_LOGIN")
                  .withColumnValue("USERNAME",login.getUserName())
                  .withColumnValue("LAST_LOGTIN_TIME",login.getLastLoginTime())
                  .withColumnValue("LOGIN_COUNT",login.getLoginCount())
                  .withColumnValue("LAST_CHANGE_PASS_TIME",login.getLastChangePassTime())
                  .withColumnValue("LAST_SIX_PASSWORD",login.getLastSixPassword());
          insertion.insert(this.jdbcTemplate);
      }

  }
}
