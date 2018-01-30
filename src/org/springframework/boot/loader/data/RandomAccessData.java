package org.springframework.boot.loader.data;

import java.io.IOException;
import java.io.InputStream;

public abstract interface RandomAccessData
{
  public abstract InputStream getInputStream(ResourceAccess paramResourceAccess)
    throws IOException;
  
  public abstract RandomAccessData getSubsection(long paramLong1, long paramLong2);
  
  public abstract long getSize();
  
  public static enum ResourceAccess
  {
    ONCE,  PER_READ;
    
    private ResourceAccess() {}
  }
}
