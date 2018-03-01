package com.vw.isms.standard.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;

public abstract class SimpleJdbcQuery<T>
  extends SimpleJdbcOperation
  implements RowMapper<T>
{
    protected String orderBy;

    public SimpleJdbcQuery<T> withOrderBy(String orderBy){
        this.orderBy = orderBy;
        return this;
    }

  public List<T> query(NamedParameterJdbcTemplate jdbcTemplate)
  {
    return jdbcTemplate.query(getSql(), this.keys, this);
  }
  
  public T queryForObject(NamedParameterJdbcTemplate jdbcTemplate)
  {
    return jdbcTemplate.queryForObject(getSql(), this.keys, this);
  }
  
  private String getSql()
  {
    StringBuilder builder = new StringBuilder();
    if ((this.columns.isEmpty()) || (this.columns.containsKey("*")))
    {
      builder.append("SELECT * ");
    }
    else
    {
      builder.append("SELECT ");
      Collection<String> params = GetNamedParameters(this.keys.keySet());
      params.addAll(GetNamedParameters(this.columns.keySet()));
      builder.append(StringUtils.collectionToDelimitedString(params, ","));
    }
    builder.append(" FROM ");
    builder.append(this.schema);
    builder.append(".");
    builder.append(this.table);
    builder.append(" WHERE ");
    builder.append(StringUtils.collectionToDelimitedString(GetNamedParameters(this.keys.keySet()), " AND "));
    if(this.orderBy!=null&&this.orderBy.length()>0){
        builder.append(" order by ").append(this.orderBy);
    }
    return builder.toString();
  }
}
