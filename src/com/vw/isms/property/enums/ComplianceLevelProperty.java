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
@JsonTypeName("compliance_level")
public class ComplianceLevelProperty
  extends EnumProperty
{
  public static final String TYPE = "compliance_level";
  
  public static enum ComplianceLevel
  {
    COMPLIANT,  PARTIAL_COMPLIANT,  NOT_COMPLIANT,  NOT_APPLICABLE,  OTHER;
    
    private ComplianceLevel() {}
  }
  
  public static final List<EnumValue> values = new ArrayList();
  private ComplianceLevel complianceLevel;
  
  static
  {
    values.add(new EnumValue("Compliant", ComplianceLevel.COMPLIANT.toString()));
    values.add(new EnumValue("Partial Compliant", ComplianceLevel.PARTIAL_COMPLIANT.toString()));
    values.add(new EnumValue("Non Compliant", ComplianceLevel.NOT_COMPLIANT.toString()));
    values.add(new EnumValue("Inapplicable", ComplianceLevel.NOT_APPLICABLE.toString()));
    values.add(new EnumValue("Other", ComplianceLevel.OTHER.toString()));
  }
  
  @JsonCreator
  public ComplianceLevelProperty(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("readonly") boolean readonly)
  {
    super(id, name, readonly);
    setComplianceLevel(ComplianceLevel.NOT_APPLICABLE);
  }
  
  public String getValue()
  {
    return getComplianceLevel().toString();
  }
  
  public void setValue(String value)
  {
    setComplianceLevel(ComplianceLevel.valueOf(value));
  }
  
  public String getType()
  {
    return "compliance_level";
  }
  
  public List<EnumValue> getEnumValues()
  {
    return Collections.unmodifiableList(values);
  }
  
  public static ComplianceLevelProperty getProperty(Properties props, String name)
    throws ModelException
  {
    Property prop = props.getProperty(name);
    if ((prop instanceof ComplianceLevelProperty)) {
      return (ComplianceLevelProperty)prop;
    }
    throw new ModelException(prop + " is not a ComplianceLevelProperty.");
  }
  
  @JsonIgnore
  public ComplianceLevel getComplianceLevel()
  {
    return this.complianceLevel;
  }
  
  @JsonIgnore
  public void setComplianceLevel(ComplianceLevel complianceLevel)
  {
    this.complianceLevel = complianceLevel;
  }
  
  public Property makeCopy()
  {
    ComplianceLevelProperty prop = new ComplianceLevelProperty(IdUtil.next(), this.name, this.readonly);
    prop.complianceLevel = this.complianceLevel;
    return prop;
  }
}
