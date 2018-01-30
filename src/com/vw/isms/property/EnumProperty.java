package com.vw.isms.property;

import com.vw.isms.ModelException;
import com.vw.isms.RepositoryException;
import com.vw.isms.property.enums.ApplicableProperty;
import com.vw.isms.property.enums.ComplianceLevelProperty;
import com.vw.isms.property.enums.LevelApprovedProperty;
import com.vw.isms.property.enums.SeverityLevelProperty;
import com.vw.isms.standard.model.StandardNode;
import com.vw.isms.standard.repository.StandardRepository;
import java.util.List;

public abstract class EnumProperty
  extends Property
{
  public EnumProperty(long id, String name, boolean readonly)
  {
    super(id, name, readonly);
  }
  
  public abstract String getValue();
  
  public abstract void setValue(String paramString);
  
  public String getDisplayValue()
  {
    for (EnumValue ev : getEnumValues()) {
      if (ev.getValue().equals(getValue())) {
        return ev.getDisplayValue();
      }
    }
    return null;
  }
  
  public abstract List<EnumValue> getEnumValues();
  
  public void update(StandardNode node, StandardRepository repo)
    throws RepositoryException
  {
    repo.updateEnumProperty(node, this);
  }
  
  public void create(StandardNode node, StandardRepository repo)
    throws RepositoryException
  {
    repo.createEnumProperty(node, this);
  }
  
  public static EnumProperty newProperty(String type, long id, String name, boolean readonly)
    throws ModelException
  {
    if (type.equals("severity_level")) {
      return new SeverityLevelProperty(id, name, readonly);
    }
    if (type.equals("compliance_level")) {
      return new ComplianceLevelProperty(id, name, readonly);
    }
    if (type.equals("level_approved")) {
      return new LevelApprovedProperty(id, name, readonly);
    }
    if (type.equals("applicable")) {
      return new ApplicableProperty(id, name, readonly);
    }
    throw new ModelException("Unknown Enum type " + type);
  }
}
