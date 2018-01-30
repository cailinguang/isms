package com.vw.isms.standard.repository;

import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;

public class SimpleJdbcUpdate
  extends SimpleJdbcOperation
{
  public void update(NamedParameterJdbcTemplate template)
    throws DataAccessException
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(this.schema);
    builder.append(".");
    builder.append(this.table);
    builder.append(" SET ");
    builder.append(StringUtils.collectionToDelimitedString(GetNamedParameters(this.columns.keySet()), ","));
    builder.append(" WHERE ");
    builder.append(StringUtils.collectionToDelimitedString(GetNamedParameters(this.keys.keySet()), " AND "));
    this.columns.putAll(this.keys);
    template.update(builder.toString(), this.columns);
  }
}
