package com.vw.isms.standard.repository;

import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.User;
import com.vw.isms.util.PasswordUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
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
  
  public String createUser(User user)
    throws RepositoryException
  {
    try
    {
      String rawPassword = PasswordUtil.randomCompliantPassword();
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
      builder.append("SELECT * FROM APP.ISMS_USERS");
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
          return user;
        }

          @Override
          public int count() {
              return namedTemplate.queryForObject(builder.toString().replace("*","count(*)"),values,Integer.class);
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
    SimpleJdbcQuery<User> query = new SimpleJdbcQuery()
    {
      public User mapRow(ResultSet rs, int rowNum)
        throws SQLException
      {
        User user = new User();
        user.setUserName(rs.getString("USERNAME"));
        user.setPassword(rs.getString("PASSWORD"));
        user.setRole(rs.getString("ROLE"));
        user.setEmail(rs.getString("EMAIL"));
        user.setRealName(rs.getString("REALNAME"));
        user.setTel(rs.getString("TEL"));
        user.setDepartment(rs.getString("DEPARTMENT"));
        return user;
      }
    };
    try
    {
      query.withSchema("APP").withTable("ISMS_USERS").withKey("USERNAME", username).withColumn("*");
      return (User)query.queryForObject(this.namedTemplate);
    }
    catch (Throwable t)
    {
      throw new RepositoryException(t.getMessage());
    }
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
    if (PasswordUtil.isCompliantPassword(newPassword))
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
      String rawPassword = PasswordUtil.randomCompliantPassword();
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
        .withColumnValue("TEL", user.getTel());
      update.update(this.namedTemplate);
    }
    catch (Throwable t)
    {
      t.printStackTrace();
      throw new RepositoryException(t.getMessage());
    }
  }
}
