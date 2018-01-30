package com.vw.isms.standard.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class Evidence
{
  private long id;
  private String name;
  private String description;
  private String path;
  private String contentType;
  
  public String getContentType()
  {
    return this.contentType;
  }
  
  public void setContentType(String contentType)
  {
    this.contentType = contentType;
  }
  
  @JsonSerialize(using=ToStringSerializer.class)
  public long getId()
  {
    return this.id;
  }
  
  public void setId(long id)
  {
    this.id = id;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String getPath()
  {
    return this.path;
  }
  
  public void setPath(String path)
  {
    this.path = path;
  }
}
