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
@JsonTypeName("severity_level")
public class SeverityLevelProperty
  extends EnumProperty
{
  public static final String TYPE = "severity_level";
  
  public static enum SeverityLevel
  {
    HIGH,  MEDIUM,  LOW,  SL,  NONE;
    
    private SeverityLevel() {}
  }
  
  public static final List<EnumValue> values = new ArrayList();
  private SeverityLevel severityLevel;
  
  static
  {
    values.add(new EnumValue("High", SeverityLevel.HIGH.toString()));
    values.add(new EnumValue("Medium", SeverityLevel.MEDIUM.toString()));
    values.add(new EnumValue("Low", SeverityLevel.LOW.toString()));
    values.add(new EnumValue("SL", SeverityLevel.SL.toString()));
    values.add(new EnumValue("None", SeverityLevel.NONE.toString()));
  }
  
  @JsonCreator
  public SeverityLevelProperty(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("readonly") boolean readonly)
  {
    super(id, name, readonly);
    this.severityLevel = SeverityLevel.SL;
  }
  
  public String getValue()
  {
    return getSeverityLevel().toString();
  }
  
  public void setValue(String value)
  {
    setSeverityLevel(SeverityLevel.valueOf(value));
  }
  
  public String getType()
  {
    return "severity_level";
  }
  
  public List<EnumValue> getEnumValues()
  {
    return Collections.unmodifiableList(values);
  }
  
  public static SeverityLevelProperty getProperty(Properties props, String name)
    throws ModelException
  {
    Property prop = props.getProperty(name);
    if ((prop instanceof SeverityLevelProperty)) {
      return (SeverityLevelProperty)prop;
    }
    throw new ModelException(prop + " is not a EnumProperty.");
  }
  
  @JsonIgnore
  public SeverityLevel getSeverityLevel()
  {
    return this.severityLevel;
  }
  
  @JsonIgnore
  public void setSeverityLevel(SeverityLevel severityLevel)
  {
    this.severityLevel = severityLevel;
  }
  
  public Property makeCopy()
  {
    SeverityLevelProperty prop = new SeverityLevelProperty(IdUtil.next(), this.name, this.readonly);
    prop.severityLevel = this.severityLevel;
    return prop;
  }
}
