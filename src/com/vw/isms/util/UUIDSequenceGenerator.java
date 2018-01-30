package com.vw.isms.util;

import java.util.UUID;

class UUIDSequenceGenerator
  implements SequenceGenerator
{
  public long next()
  {
    return Math.abs(UUID.randomUUID().getMostSignificantBits()) + 1L;
  }
}
