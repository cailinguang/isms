package com.vw.isms.property;

import com.vw.isms.ModelException;
import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.repository.StandardRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Properties
{
  private Map<String, Property> properties = new HashMap();
  
  public void addProperty(Property property)
  {
    this.properties.put(property.getName(), property);
  }
  
  public Map<String, Property> getProperties()
  {
    return Collections.unmodifiableMap(this.properties);
  }
  
  public void updateProperties(StandardNode node, StandardRepository repo)
    throws RepositoryException
  {
    for (Property prop : this.properties.values()) {
      prop.update(node, repo);
    }
  }
  
  public void createProperties(StandardNode node, StandardRepository repo)
    throws RepositoryException
  {
    for (Property prop : this.properties.values()) {
      prop.create(node, repo);
    }
  }
  
  public Property getProperty(String propertyName)
    throws ModelException
  {
    if (this.properties.containsKey(propertyName)) {
      return (Property)this.properties.get(propertyName);
    }
    throw new ModelException("Does not have property: " + propertyName);
  }
  
  public Property getPropertyOrNull(String propertyName)
    throws ModelException
  {
    if (this.properties.containsKey(propertyName)) {
      return (Property)this.properties.get(propertyName);
    }
    return null;
  }
  
  public void copyPropertiesFrom(Properties that)
  {
    for (Property prop : that.properties.values()) {
      addProperty(prop.makeCopy());
    }
  }
  
  public void removeProperty(String name)
  {
    this.properties.remove(name);
  }
}
