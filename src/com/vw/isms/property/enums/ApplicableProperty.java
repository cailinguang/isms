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
@JsonTypeName("applicable")
public class ApplicableProperty
  extends EnumProperty
{
  public static final String TYPE = "applicable";
  
  public static enum Applicable
  {
    NOT_EVALUATED,  APPLICABLE,  NOT_APPLICABLE;
    
    private Applicable() {}
  }
  
  public static final List<EnumValue> values = new ArrayList();
  private Applicable applicable;
  
  static
  {
    values.add(new EnumValue("Unevaluated", Applicable.NOT_EVALUATED.toString()));
    values.add(new EnumValue("Applicable", Applicable.APPLICABLE.toString()));
    values.add(new EnumValue("Inapplicable", Applicable.NOT_APPLICABLE.toString()));
  }
  
  @JsonCreator
  public ApplicableProperty(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("readonly") boolean readonly)
  {
    super(id, name, readonly);
    this.applicable = Applicable.NOT_EVALUATED;
  }
  
  public String getValue()
  {
    return this.applicable.toString();
  }
  
  public void setValue(String value)
  {
    this.applicable = Applicable.valueOf(value);
  }
  
  public String getType()
  {
    return "applicable";
  }
  
  public List<EnumValue> getEnumValues()
  {
    return Collections.unmodifiableList(values);
  }
  
  public static ApplicableProperty getProperty(Properties props, String name)
    throws ModelException
  {
    Property prop = props.getProperty(name);
    if ((prop instanceof ApplicableProperty)) {
      return (ApplicableProperty)prop;
    }
    throw new ModelException(prop + " is not a ApplicableProperty.");
  }
  
  @JsonIgnore
  public Applicable getApplicable()
  {
    return this.applicable;
  }
  
  @JsonIgnore
  public void setApplicable(Applicable applicable)
  {
    this.applicable = applicable;
  }
  
  public Property makeCopy()
  {
    ApplicableProperty prop = new ApplicableProperty(IdUtil.next(), this.name, this.readonly);
    prop.applicable = this.applicable;
    return prop;
  }
}
