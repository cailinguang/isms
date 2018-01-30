package com.vw.isms.property;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.vw.isms.ModelException;
import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.util.IdUtil;

@JsonTypeName("string")
public class StringProperty
  extends Property
{
  public static final String TYPE = "string";
  private String value;
  
  @JsonCreator
  public StringProperty(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("readonly") boolean readonly)
  {
    super(id, name, readonly);
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public void update(StandardNode node, StandardRepository repo)
    throws RepositoryException
  {
    if (getId() == 0L) {
      return;
    }
    repo.updateStringProperty(node, this);
  }
  
  public void create(StandardNode node, StandardRepository repo)
    throws RepositoryException
  {
    if (getId() == 0L) {
      return;
    }
    repo.createStringProperty(node, this);
  }
  
  public static StringProperty getProperty(Properties props, String name)
    throws ModelException
  {
    Property prop = props.getProperty(name);
    if ((prop instanceof StringProperty)) {
      return (StringProperty)prop;
    }
    throw new ModelException(prop + " is not a StringProperty.");
  }
  
  public static StringProperty getPropertyOrNull(Properties props, String name)
    throws ModelException
  {
    Property prop = props.getPropertyOrNull(name);
    if (prop == null) {
      return null;
    }
    if ((prop instanceof StringProperty)) {
      return (StringProperty)prop;
    }
    throw new ModelException(prop + " is not a StringProperty.");
  }
  
  public String getType()
  {
    return "string";
  }
  
  public Property makeCopy()
  {
    StringProperty prop = new StringProperty(IdUtil.next(), this.name, this.readonly);
    prop.value = this.value;
    return prop;
  }
}
