package org.springframework.boot.loader.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteArrayRandomAccessData
  implements RandomAccessData
{
  private final byte[] bytes;
  private final long offset;
  private final long length;
  
  public ByteArrayRandomAccessData(byte[] bytes)
  {
    this(bytes, 0L, bytes == null ? 0 : bytes.length);
  }
  
  public ByteArrayRandomAccessData(byte[] bytes, long offset, long length)
  {
    this.bytes = (bytes == null ? new byte[0] : bytes);
    this.offset = offset;
    this.length = length;
  }
  
  public InputStream getInputStream(RandomAccessData.ResourceAccess access)
  {
    return new ByteArrayInputStream(this.bytes, (int)this.offset, (int)this.length);
  }
  
  public RandomAccessData getSubsection(long offset, long length)
  {
    return new ByteArrayRandomAccessData(this.bytes, this.offset + offset, length);
  }
  
  public long getSize()
  {
    return this.length;
  }
}
