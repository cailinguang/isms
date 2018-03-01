package com.vw.isms.standard.repository;

import java.util.Map;
import java.util.logging.Logger;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;

public class SimpleJdbcUpdate
  extends SimpleJdbcOperation
{
    private final Logger logger = Logger.getLogger(getClass().getName());

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
    int count = template.update(builder.toString(), this.columns);
    if(count==0){
        logger.info("update 0 data!");
    }
  }
}
