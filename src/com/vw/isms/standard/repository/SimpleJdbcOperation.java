package com.vw.isms.standard.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class SimpleJdbcOperation
{
  protected String schema;
  protected String table;
  protected final Map<String, Object> keys = new HashMap();
  protected final Map<String, Object> columns = new HashMap();
  
  public SimpleJdbcOperation withSchema(String schema)
  {
    this.schema = schema;
    return this;
  }
  
  public SimpleJdbcOperation withTable(String table)
  {
    this.table = table;
    return this;
  }
  
  public SimpleJdbcOperation withKey(String name, Object value)
  {
    this.keys.put(name, value);
    return this;
  }
  
  public SimpleJdbcOperation withColumnValue(String name, Object value)
  {
    this.columns.put(name, value);
    return this;
  }
  
  public SimpleJdbcOperation withColumn(String name)
  {
    this.columns.put(name, null);
    return this;
  }
  
  protected Collection<String> GetNamedParameters(Collection<String> names)
  {
    Collection<String> results = new ArrayList();
    for (String name : names) {
      results.add(name + "=:" + name);
    }
    return results;
  }
}
