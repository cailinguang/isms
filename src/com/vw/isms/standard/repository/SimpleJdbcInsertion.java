package com.vw.isms.standard.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class SimpleJdbcInsertion
  extends SimpleJdbcOperation
{
  public void insert(JdbcTemplate jdbcTemplate)
  {
    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
    jdbcInsert.setSchemaName(this.schema);
    jdbcInsert.setTableName(this.table);
    jdbcInsert.execute(this.columns);
  }
}
