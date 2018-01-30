package com.vw.isms.property;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.repository.StandardRepository;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({@com.fasterxml.jackson.annotation.JsonSubTypes.Type(value=StringProperty.class, name="string"), @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value=EvidenceSetProperty.class, name="evidences"), @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value=BooleanProperty.class, name="boolean"), @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value=FloatProperty.class, name="float"), @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value=com.vw.isms.property.enums.ComplianceLevelProperty.class, name="compliance_level"), @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value=com.vw.isms.property.enums.LevelApprovedProperty.class, name="level_approved"), @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value=com.vw.isms.property.enums.SeverityLevelProperty.class, name="severity_level"), @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value=com.vw.isms.property.enums.ApplicableProperty.class, name="applicable")})
@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class Property
{
  protected final String name;
  protected final long id;
  protected final boolean readonly;
  
  public Property(long id, String name, boolean readonly)
  {
    this.id = id;
    this.name = name;
    this.readonly = readonly;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  @JsonSerialize(using=ToStringSerializer.class)
  public long getId()
  {
    return this.id;
  }
  
  public boolean isReadonly()
  {
    return this.readonly;
  }
  
  public String getSearchUri()
  {
    return "api/properties/" + getType();
  }
  
  public abstract String getType();
  
  public abstract void update(StandardNode paramStandardNode, StandardRepository paramStandardRepository)
    throws RepositoryException;
  
  public abstract void create(StandardNode paramStandardNode, StandardRepository paramStandardRepository)
    throws RepositoryException;
  
  public abstract Property makeCopy();
}
