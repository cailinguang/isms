package com.vw.isms.standard.repository;

import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;

public class SimpleJdbcDelete
  extends SimpleJdbcOperation
{
  public void delete(NamedParameterJdbcTemplate template)
    throws DataAccessException
  {
    StringBuilder builder = new StringBuilder();
    builder.append("DELETE FROM ");
    builder.append(this.schema);
    builder.append(".");
    builder.append(this.table);
    builder.append(" WHERE ");
    builder.append(StringUtils.collectionToDelimitedString(GetNamedParameters(this.keys.keySet()), " AND "));
    template.update(builder.toString(), this.keys);
  }
}
