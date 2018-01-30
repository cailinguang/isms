package com.vw.isms.property;

public class EnumValue
{
  private final String displayValue;
  private final String value;
  
  public EnumValue(String displayValue, String value)
  {
    this.displayValue = displayValue;
    this.value = value;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public String getDisplayValue()
  {
    return this.displayValue;
  }
}
