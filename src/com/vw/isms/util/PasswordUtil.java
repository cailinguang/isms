package com.vw.isms.util;

import org.apache.commons.lang3.RandomStringUtils;

public class PasswordUtil
{
  public static boolean hasUpper(String s)
  {
    for (int i = 0; i < s.length(); i++) {
      if (isUpper(s.charAt(i))) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isUpper(char c)
  {
    return (c >= 'A') && (c <= 'Z');
  }
  
  public static boolean hasLower(String s)
  {
    for (int i = 0; i < s.length(); i++) {
      if (isLower(s.charAt(i))) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isLower(char c)
  {
    return (c >= 'a') && (c <= 'z');
  }
  
  public static boolean hasNumber(String s)
  {
    for (int i = 0; i < s.length(); i++) {
      if (isNumber(s.charAt(i))) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isNumber(char c)
  {
    return (c >= '0') && (c <= '9');
  }
  
  public static boolean hasNonAlphaNumeric(String s)
  {
    for (int i = 0; i < s.length(); i++) {
      if ((!isLower(s.charAt(i))) && (!isUpper(s.charAt(i))) && (!isNumber(s.charAt(i)))) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isCompliantPassword(String password)
  {
    if (password.length() < 8) {
      return false;
    }
    int complianceCount = 0;
    if (hasUpper(password)) {
      complianceCount++;
    }
    if (hasLower(password)) {
      complianceCount++;
    }
    if (hasNumber(password)) {
      complianceCount++;
    }
    if (hasNonAlphaNumeric(password)) {
      complianceCount++;
    }
    return complianceCount >= 3;
  }
  
  public static String randomCompliantPassword()
  {
    String password;
    do
    {
      password = RandomStringUtils.randomAlphanumeric(8);
    } while (!isCompliantPassword(password));
    return password;
  }
}
