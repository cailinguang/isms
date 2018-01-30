package org.springframework.boot.loader;

import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.Archive.Entry;
import org.springframework.boot.loader.util.AsciiBytes;

public class WarLauncher
  extends ExecutableArchiveLauncher
{
  private static final AsciiBytes WEB_INF = new AsciiBytes("WEB-INF/");
  private static final AsciiBytes WEB_INF_CLASSES = WEB_INF.append("classes/");
  private static final AsciiBytes WEB_INF_LIB = WEB_INF.append("lib/");
  private static final AsciiBytes WEB_INF_LIB_PROVIDED = WEB_INF
    .append("lib-provided/");
  
  public WarLauncher() {}
  
  protected WarLauncher(Archive archive)
  {
    super(archive);
  }
  
  public boolean isNestedArchive(Archive.Entry entry)
  {
    if (entry.isDirectory()) {
      return entry.getName().equals(WEB_INF_CLASSES);
    }
    return (entry.getName().startsWith(WEB_INF_LIB)) || (entry.getName().startsWith(WEB_INF_LIB_PROVIDED));
  }
  
  public static void main(String[] args)
  {
    new WarLauncher().launch(args);
  }
}
