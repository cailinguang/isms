package com.vw.isms.standard.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@JsonSerialize(using = CustomJsonSerial.class)
public class Evidence
{
  private long id;
  private String name;
  private String description;
  private String path;
  private String contentType;

  private long classId;

  private String className;
  
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

  public long getClassId() {
    return classId;
  }

  public void setClassId(long classId) {
    this.classId = classId;
  }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
