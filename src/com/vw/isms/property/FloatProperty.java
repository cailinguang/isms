package com.vw.isms.property;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.vw.isms.ModelException;
import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.util.IdUtil;

@JsonTypeName("float")
public class FloatProperty
  extends Property
{
  public static final String TYPE = "float";
  private double value;
  
  @JsonCreator
  public FloatProperty(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("readonly") boolean readonly)
  {
    super(id, name, readonly);
  }
  
  public double getValue()
  {
    return this.value;
  }
  
  public void setValue(double value)
  {
    this.value = value;
  }
  
  public void update(StandardNode node, StandardRepository repo)
    throws RepositoryException
  {
    repo.updateFloatProperty(node, this);
  }
  
  public void create(StandardNode node, StandardRepository repo)
    throws RepositoryException
  {
    repo.createFloatProperty(node, this);
  }
  
  public static FloatProperty getProperty(Properties props, String name)
    throws ModelException
  {
    Property prop = props.getProperty(name);
    if ((prop instanceof FloatProperty)) {
      return (FloatProperty)prop;
    }
    throw new ModelException(prop + " is not a FloatProperty.");
  }
  
  public String getType()
  {
    return "float";
  }
  
  public Property makeCopy()
  {
    FloatProperty prop = new FloatProperty(IdUtil.next(), this.name, this.readonly);
    prop.value = this.value;
    return prop;
  }
  
  public static FloatProperty getPropertyOrNull(Properties props, String name)
    throws ModelException
  {
    Property prop = props.getPropertyOrNull(name);
    if (prop == null) {
      return null;
    }
    if ((prop instanceof FloatProperty)) {
      return (FloatProperty)prop;
    }
    throw new ModelException(prop + " is not a FloatProperty.");
  }
}
