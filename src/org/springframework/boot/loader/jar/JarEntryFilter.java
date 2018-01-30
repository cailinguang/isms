package org.springframework.boot.loader.jar;

import org.springframework.boot.loader.util.AsciiBytes;

public abstract interface JarEntryFilter
{
  public abstract AsciiBytes apply(AsciiBytes paramAsciiBytes, JarEntryData paramJarEntryData);
}
