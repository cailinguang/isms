package com.vw.isms.property.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.vw.isms.ModelException;
import com.vw.isms.property.EnumProperty;
import com.vw.isms.property.EnumValue;
import com.vw.isms.property.Properties;
import com.vw.isms.property.Property;
import com.vw.isms.util.IdUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(value={"enumValues", "type", "searchUri"}, allowGetters=true, allowSetters=false)
@JsonTypeName("level_approved")
public class LevelApprovedProperty
  extends EnumProperty
{
  public static final String TYPE = "level_approved";
  
  public static enum LevelApproved
  {
    YES,  NO;
    
    private LevelApproved() {}
  }
  
  public static final List<EnumValue> values = new ArrayList();
  private LevelApproved levelApproved;
  
  static
  {
    values.add(new EnumValue("Yes", LevelApproved.YES.toString()));
    values.add(new EnumValue("No", LevelApproved.NO.toString()));
  }
  
  @JsonCreator
  public LevelApprovedProperty(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("readonly") boolean readonly)
  {
    super(id, name, readonly);
    this.levelApproved = LevelApproved.NO;
  }
  
  public String getValue()
  {
    return this.levelApproved.toString();
  }
  
  public void setValue(String value)
  {
    this.levelApproved = LevelApproved.valueOf(value);
  }
  
  public String getType()
  {
    return "level_approved";
  }
  
  public List<EnumValue> getEnumValues()
  {
    return Collections.unmodifiableList(values);
  }
  
  public static LevelApprovedProperty getProperty(Properties props, String name)
    throws ModelException
  {
    Property prop = props.getProperty(name);
    if ((prop instanceof LevelApprovedProperty)) {
      return (LevelApprovedProperty)prop;
    }
    throw new ModelException(prop + " is not a LevelApprovedProperty.");
  }
  
  @JsonIgnore
  public LevelApproved getLevelApproved()
  {
    return this.levelApproved;
  }
  
  @JsonIgnore
  public void setLevelApproved(LevelApproved levelApproved)
  {
    this.levelApproved = levelApproved;
  }
  
  public Property makeCopy()
  {
    LevelApprovedProperty prop = new LevelApprovedProperty(IdUtil.next(), this.name, this.readonly);
    prop.levelApproved = this.levelApproved;
    return prop;
  }
}
