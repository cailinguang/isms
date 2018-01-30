package com.vw.isms.util;

public class IdUtil
{
  private static SequenceGenerator generator = new UUIDSequenceGenerator();
  
  public static long next()
  {
    return generator.next();
  }
}
